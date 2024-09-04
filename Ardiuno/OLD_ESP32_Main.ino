#include <WiFi.h>

const char* ssid = "ENGG2K3K";

WiFiUDP udp;

IPAddress ip(10, 20, 30, 115);
IPAddress udpAddress(10, 20, 30, 1);
const int udpPort = 3015;

IPAddress dns(192, 168, 43, 1);
IPAddress gateway(192, 168, 43, 1);
IPAddress subnet(255, 255, 255, 0);

static const int motor_direction = 16;
static const int motor_speed = 17;
static const int servo = 20;
static const int ir_sensor = 21;
static const int green_led = 26;
static const int yellow_led = 27;
static const int red_led = 28;

uint8_t message[50];


void setup() {
  Serial.begin(115200);
  delay(1000);

  WiFi.config(ip, gateway, subnet, dns);
  WiFi.begin(ssid);
  Serial.println("\nConnecting");

  while(WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(100);
  }

  Serial.println("\nConnected to the WiFi network");
  Serial.print("[+] ESP32 IP : ");
  Serial.println(WiFi.localIP());


  udp.begin(udpPort);

  pinMode(motor_direction, OUTPUT);
  pinMode(motor_speed, OUTPUT);
  pinMode(servo, OUTPUT);
  pinMode(ir_sensor, INPUT);
  pinMode(green_led, OUTPUT);
  pinMode(yellow_led, OUTPUT);
  pinMode(red_led, OUTPUT);
}

void loop(){
  message = "Test";
  send_packet(message);
  receive_packet();

  delay(1000);  
  
  /*
  WiFiClient client;
  if (!client.connect("192.168.1.68", 10000)) {
      Serial.println("Connection to host failed");
      delay(1000);
      return;
  }
  Serial.println("client connected sending packet");    // <<< added
  client.print("Hello from ESP32!");
  client.stop();
  delay(1000);
*/
}

void send_packet(uint8_t buffer[50]) {
  // uint8_t buffer[50] = "Seconds since boot: ".append(millis()/1000);

  udp.beginPacket(udpAddress, udpPort);
  udp.write(buffer, 11);
  udp.endPacket();
  memset(buffer, 0, 50);
}

void receive_packet() {
  uint8_t buffer[50];
  udp.parsePacket();
  
  if (udp.read(buffer, 50) > 0) {
    Serial.print("Server to client: ");
    Serial.println((char *)buffer);
  }
}
