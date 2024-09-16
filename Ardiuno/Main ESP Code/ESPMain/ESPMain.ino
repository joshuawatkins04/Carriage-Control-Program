#define motor_direction 23
#define motor_speed 22
#define green_led 18
#define yellow_led 19
#define red_led 21
#define ir_sensor 30 //CHANGE
#define servo 31 //CHANGE

#include <WiFi.h>
#include <WiFiUdp.h>

WiFiUDP udp;

IPAddress ip(10, 20, 30, 115);
IPAddress udpAddress(10, 20, 30, 1);
IPAddress dns(192, 168, 43, 1);
IPAddress gateway(192, 168, 43, 1);
IPAddress subnet(255, 255, 255, 0);

const int udpLocalPort = 3015;
const int udpAddressPort = 3016;
char inBuffer[255];
char outBuffer[255];
char* ssid;
int state;
int current_speed;
int door_state;
int move_state;
int sensor_status;

void setup() {
  Serial.begin(115200);

  state = 0;
  current_speed = 0;
  door_state = 0;
  move_state = 0;
  ssid = (char*)"ENGG2K3K";

  WiFi.config(ip, gateway, subnet, dns);
  WiFi.begin(ssid);
  Serial.println("\nConnecting");

  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(100);
  }
  Serial.println("\nConnected to the WiFi network");
  Serial.print("[+] ESP32 IP : ");
  Serial.println(WiFi.localIP());

  udp.begin(udpLocalPort);
  pinMode(motor_speed, OUTPUT);
  pinMode(motor_direction, OUTPUT);
  pinMode(green_led, OUTPUT);
  pinMode(yellow_led, OUTPUT);
  pinMode(red_led, OUTPUT);
  pinMode(ir_sensor, INPUT);
  pinMode(servo, OUTPUT);
  digitalWrite(green_led, LOW);
  digitalWrite(yellow_led, LOW);
  digitalWrite(red_led, LOW);
}

void loop() {
  
  // Initialise connection with wifi packets
  while (state == 0) {
    Serial.println("Initialising connection with ESP and CCP");
    receive_message();
    if (inBuffer == "START") {
      send_message((char*) "STARTING");
      state = 1;
    }
    delay(1000);
  }

  // Main loop
  while (state == 1) {
    receive_message();
    if (inBuffer == "QUIT") {
      state = 2;
      break;
    } 
    else if (inBuffer == "STOP") stop_br();
    else if (inBuffer == "SLOW") slow_br();
    else if (inBuffer == "FAST") fast_br();
    
    // Check if needs to open doors only when stopped
    if (move_state == 0) {
      if (inBuffer == "OPEN_DOOR") {
        if (door_state == 0) {
          Serial.println("Opening Door");
          open_door();
        }
      } else if (inBuffer == "CLOSE_DOOR") {
        if (door_state == 1) {
          Serial.println("Closing Door");
          close_door();
        }
      }
    } 
    // Needs to check for LED to stop completely at a station???
    else if (move_state == 1) {
      detect();
      if (sensor_status >= 150 && sensor_status <= 220) {
        stop_br();
      }
    }

    delay(500);
  }
}

/* Wifi Code */
void send_message(char str[255]) {
  strcpy(outBuffer, str);
  udp.beginPacket(udpAddress, udpAddressPort);
  udp.write((const uint8_t*)outBuffer, 11);
  udp.endPacket();
}
void receive_message() {
  udp.parsePacket();
  if (udp.read(inBuffer, 255) > 0) { // udp.read is assigning a string to inBuffer in this line??
    Serial.println("Received message: ");
    Serial.print((char*) inBuffer);
    // if (inBuffer == "STOP") send_message((char*) "STOPPING");
  }
}

/* Motor Code */
void move_br(int set_speed) {
  if (set_speed > 255) set_speed = 255;  // Cap speed to 255
  if (set_speed < 0) set_speed = 0;      // Minimum speed

  analogWrite(motor_speed, set_speed);  // Set speed using PWM
  digitalWrite(motor_direction, HIGH);  // Set direction to forward

  current_speed = set_speed;  //current speed
}
void slow_br() {
  move_state = 1;
  for (int i = current_speed; i >= 100; i -= 5) { //slowdown
    analogWrite(motor_speed, i);
    delay(50);
  }
  current_speed = 100;
}
void stop_br() {
  move_state = 0;
  analogWrite(motor_speed, 0);
  current_speed = 0;
}
void fast_br() {
  move_state = 2;
  for (int i = current_speed; i <= 255; i += 5) {  //speed up 
    analogWrite(motor_speed, i);
    delay(50);
  }
  current_speed = 255;
}

/* Servo Code */
void open_door() {
  digitalWrite(servo, HIGH);
  door_state = 1;
}
void close_door() {
  digitalWrite(servo, LOW);
  door_state = 0;
}

/* IR Sensor Code */
void detect() {
  sensor_status = digitalRead(ir_sensor);
}
