#include <WiFi.h>
#include <WiFiUdp.h>

const char *ssid = "WAITWHAT";
const char *password = "WireMeUp!!!";

WiFiUDP udp;
const unsigned int localUdpPort = 4210;  // Local port to listen on
char incomingPacket[255];  // Buffer for incoming packets

IPAddress javaServerIP(192, 168, 0, 37);
unsigned int javaServerPort = 4210;

char door_status[10] = "CLOSED";
char move_status[10] = "STOPPED";

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

    send_message("(INIT) from ESP", javaServerIP, javaServerPort);
    delay(1000);

    receive_message();

    if (strcmp(incomingPacket, "(INIT Confirmed) from CCP") == 0) {
      // send_message("Initialising packet successful (FROM ESP32)", javaServerIP, javaServerPort);
      state = 1;
    }
  }


  if (state == 1) {
    send_message("STOPC", javaServerIP, javaServerPort);
    delay(1000);

    receive_message();
    Serial.printf("Java server responded: %s\n", incomingPacket);

    if (strcmp(incomingPacket, "STOPC") == 0) {
      send_message("STOPC", javaServerIP, javaServerPort);
    } else if (strcmp(incomingPacket, "STOPO") == 0) {
      send_message("STOPO", javaServerIP, javaServerPort);
    } else if (strcmp(incomingPacket, "FSLOWC") == 0) {
      send_message("STOPC", javaServerIP, javaServerPort);
    } else if (strcmp(incomingPacket, "FFASTC") == 0) {
      send_message("FFASTC", javaServerIP, javaServerPort);
    } else if (strcmp(incomingPacket, "RSLOWC") == 0) {
      send_message("STOPC", javaServerIP, javaServerPort);
    } else if (strcmp(incomingPacket, "DISCONNECT") == 0) {
      send_message("OFLN", javaServerIP, javaServerPort);
    }

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
char* get_status() {
  // static char message[50];
  // snprintf(message, sizeof(message), "(STAT) Door: %s Moving: %s", door_status, move_status);
  char* message = "STOPC";
  return message;
}



