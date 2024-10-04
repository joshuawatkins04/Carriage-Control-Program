#include <WiFi.h>
#include <WiFiUdp.h>

const char *ssid = "WAITWHAT";
const char *password = "WireMeUp!!!";

WiFiUDP udp;
const unsigned int localUdpPort = 4210;  // Local port to listen on
char incomingPacket[255];  // Buffer for incoming packets

IPAddress javaServerIP(192, 168, 0, 37);
unsigned int javaServerPort = 4210;

void setup() {
  Serial.begin(115200);
  Serial.println();

  // Connect to Wi-Fi
  Serial.printf("Connecting to %s ", ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println(" connected");

  // Start listening for UDP packets
  udp.begin(localUdpPort);
  Serial.printf("Now listening at IP %s, UDP port %d\n", WiFi.localIP().toString().c_str(), localUdpPort);
}

int state = 0;

void loop() {

  if (state == 0) {
    Serial.println("Initialising connection with ESP and Java Server");

    send_message("INIT from ESP", javaServerIP, javaServerPort);
    delay(1000);

    receive_message();

    if (strcmp(incomingPacket, "INIT from Java") == 0) {
      send_message("Initialising packet successful (FROM ESP32)", javaServerIP, javaServerPort);
      state = 1;
    }
  }


  if (state == 1) {
    send_message("COMMAND_1", javaServerIP, javaServerPort);
    delay(1000);

    receive_message();
    Serial.printf("Java server responded: %s\n", incomingPacket);

    delay(2000);
  }

}

void send_message(const char* str, IPAddress remoteIP, unsigned int remotePort) {
  udp.beginPacket(remoteIP, remotePort);
  udp.write((uint8_t*)str, strlen(str));
  udp.endPacket();
  Serial.printf("Sent a UDP packet to %s, port %d\n", remoteIP.toString().c_str(), remotePort);
}
void receive_message() {
  int packetSize = udp.parsePacket();
  if (packetSize) {
    int len = udp.read(incomingPacket, 255);
    if (len > 0) {
      incomingPacket[len] = 0;
    }
    Serial.printf("Received %d bytes from %s, port %d\n", packetSize, udp.remoteIP().toString().c_str(), udp.remotePort());
    Serial.printf("UDP packet contents: %s\n", incomingPacket);
  }
}


// #include <WiFi.h>
// #include <WiFiUdp.h>

// WiFiUDP udp;

// IPAddress ip(10, 20, 30, 115);
// IPAddress udpAddress(10, 20, 30, 1);
// IPAddress dns(192, 168, 43, 1);
// IPAddress gateway(192, 168, 43, 1);
// IPAddress subnet(255, 255, 255, 0);

// const int udpLocalPort = 3015;
// const int udpAddressPort = 3016;
// char inBuffer[255];
// char outBuffer[255];
// char* ssid;

// void setup() {
//   Serial.begin(115200);

//   ssid = (char*)"ENGG2K3K";

//   WiFi.config(ip, gateway, subnet, dns);
//   WiFi.begin(ssid);
//   Serial.println("\nConnecting");

//   while (WiFi.status() != WL_CONNECTED) {
//     Serial.print(".");
//     delay(100);
//   }
//   Serial.println("\nConnected to the WiFi network");
//   Serial.print("[+] ESP32 IP : ");
//   Serial.println(WiFi.localIP());

//   udp.begin(udpLocalPort);
// }

// int state = 0;
// void loop() {
//   while (state == 0) {
//     Serial.println("Initialising connection with ESP and CCP");
//     receive_message();
//     if (inBuffer == "START") {
//       send_message((char*) "STARTING");
//       state = 1;
//     }
//     delay(1000);
//   }

//   while (state == 1) {
//     Serial.println("Connection established");
//     delay(2000);
//   }
// }

// /* Wifi Code */
// void send_message(char str[255]) {
//   strcpy(outBuffer, str);
//   udp.beginPacket(udpAddress, udpAddressPort);
//   udp.write((const uint8_t*)outBuffer, 11);
//   udp.endPacket();
// }
// void receive_message() {
//   udp.parsePacket();
//   if (udp.read(inBuffer, 255) > 0) { // udp.read is assigning a string to inBuffer in this line??
//     Serial.println("Received message: ");
//     Serial.print((char*) inBuffer);
//     // if (inBuffer == "STOP") send_message((char*) "STOPPING");
//   } else {
//     Serial.println("Message was not received");
//   }
// }



