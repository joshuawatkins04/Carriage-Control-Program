#include <WiFi.h>
#include <WiFiUdp.h>

WiFiUDP udp;

char* ssid = (char*)"ENGG2K3K";

//IPAddress ip(10, 20, 30, 115);
//IPAddress udpAddress(10, 20, 30, 1);
IPAddress WiFi.localIP();
IPAddress WiFi.localIP();

const int udpLocalPort = 3015;
const int udpAddressPort = 4445;

IPAddress dns(192, 168, 43, 1);
IPAddress gateway(192, 168, 43, 1);
IPAddress subnet(255, 255, 255, 0);

char inBuffer[255];
char outBuffer[255];


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
  
  
  udp.begin(udpLocalPort);

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
      sendMsg((char*)"STARTING");
    }
    if(inBuffer == "STOP") {
      sendMsg((char*)"STOPPING");
    }
  }
}

void loop(){


  sendMsg((char*)"Seconds since boot: " + millis()/1000);
  recieveMsg();
  
  delay(1000); 
}