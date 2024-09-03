#include <WiFi.h>

const char* ssid = "ENGG2K3K";

IPAddress ip(10, 20, 30, 115);
IPAddress udpAddress(10, 20, 30, 1);
const int udpPort = 3015;

IPAddress dns(192, 168, 43, 1);
IPAddress gateway(192, 168, 43, 1);
IPAddress subnet(255, 255, 255, 0);

void setup(){
Serial.begin(115200);
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


udp.begin(udpPort);

}

void loop(){
  WiFiUDP udp;

  uint8_t buffer[50] = "Seconds since boot: ".append(millis()/1000);
  udp.beginPacket(udpAddress, udpPort);
  udp.write(buffer, 11);
  udp.endPacket();
  memset(buffer, 0, 50);

  
  udp.parsePacket();
  if(udp.read(buffer, 50) > 0){
    Serial.print("Server to client: ");
    Serial.println((char *)buffer);
  }
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
