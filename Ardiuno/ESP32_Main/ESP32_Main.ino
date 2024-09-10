#define motor_direction 16
#define motor_speed 17
#define servo 20
#define ir_sensor 21
#define green_led 22
#define yellow_led 23
#define red_led 26

#include <Motor_Controller.h>
#include <IR_Sensor.h>
#include <Servo_Controller.h>
#include <WiFi.h>
#include <WiFiUdp.h>

// Initialise Classes and assigns pins to each
Motor_Controller mc(motor_direction, motor_speed);
IR_Sensor ir(ir_sensor);
Servo_Controller sc(servo);

WiFiUDP udp;

const char* ssid = "ENGG2K3K";

IPAddress ip(10, 20, 30, 115);
IPAddress udpAddress(10, 20, 30, 1);
const int udpLocalPort = 3015;
const int udpAddressPort = 3016;

IPAddress dns(192, 168, 43, 1);
IPAddress gateway(192, 168, 43, 1);
IPAddress subnet(255, 255, 255, 0);

char inBuffer[255];
char outBuffer[255];

// Setup
void setup() {
  Serial.begin(115200);

  pinMode(green_led, OUTPUT);
  pinMode(yellow_led, OUTPUT);
  pinMode(red_led, OUTPUT);

  delay(1000);
  
  WiFi.config(ip, gateway, subnet, dns);
  WiFi.begin(ssid);
  Serial.println("\nConnecting");
  
  while(WiFi.status() != WL_CONNECTED){
      Serial.print(".");
      delay(100);
  }
  
  Serial.println("\nConnected to the WiFi network");
  Serial.print("[+] ESP32 IP : ");
  Serial.println(WiFi.localIP());
  
  udp.begin(udpLocalPort);
}

// Main loop
void loop() {
  Serial.println("\nBeginning main loop");
  
  // Tests
  test_motor();
  test_ir();
  test_servo();
}

void test_motor() {
  Serial.println("\n\n\n\nTesting Motor Controller");
  Serial.println("Starting Motor, set speed 0");
  mc.moveBR(0);
  delay(3000);
  Serial.println("Starting Motor, set speed 50 for 5 seconds");
  mc.moveBR(50);
  delay(5000);
  mc.stopBR();
  Serial.println("Stopping motor");
  delay(3000);
}

void test_ir() {
  Serial.println("\n\n\n\nTesting IR Sensor");
  ir.detect();
}

void test_servo() {
  Serial.println("\n\n\n\nTesting Servo Motor");
  Serial.println("Door state is: ");
  Serial.print(sc.getDoorState());
  delay(1000);
  Serial.println("Changing door state to open");
  sc.openDoor();
  Serial.println("Door state is: ");
  Serial.print(sc.getDoorState());
  delay(3000);
  Serial.println("Changing door state to closed");
  sc.closeDoor();
  Serial.println("Door state is: ");
  Serial.print(sc.getDoorState());
  delay(3000);
}

void sendMsg(char str[255]){
  strcpy(outBuffer, str);
  udp.beginPacket(udpAddress, udpAddressPort);
  udp.write(outBuffer, 11);
  udp.endPacket();
  //memset(outBuffer, 0, 50);
}

void recieveMsg(){
  udp.parsePacket();
  if(udp.read(inBuffer, 255) > 0){
    Serial.print("Server to client: ");
    Serial.println((char *)inBuffer);
    if(inBuffer == "START") {
      sendMsg("STARTING");
    }
    if(inBuffer == "STOP") {
      sendMsg("STOPPING");
    }
  }
}