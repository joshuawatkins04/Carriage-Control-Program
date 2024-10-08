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

const char *ssid = "WIFI NAME";
const char *password = "WIFI PASSWORD"; 
const unsigned int localUdpPort = 4210;  // Local port to listen on
char incomingPacket[255];  // Buffer for incoming packets

IPAddress javaServerIP(192, 168, 0, 37);
unsigned int javaServerPort = 4210;

char door_status[10] = "CLOSED";
char move_status[10] = "STOPPED";

int state;
int current_speed;
int door_state;
int move_state;
int sensor_status;

void setup() {
  Serial.begin(115200);
  Serial.println();

  state = 0;
  current_speed = 0;
  door_state = 0;
  move_state = 0;

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
    Serial.println("Initialising connection with ESP and Java Server");

    send_message("(INIT) from ESP", javaServerIP, javaServerPort);
    delay(1000);

    receive_message();

    if (strcmp(incomingPacket, "(INIT Confirmed) from CCP") == 0) {
      // send_message("Initialising packet successful (FROM ESP32)", javaServerIP, javaServerPort);
      state = 1;
    }
  }

  // Main loop
  while (state == 1) {
    receive_message();

    send_message("STOPC", javaServerIP, javaServerPort);
    delay(1000);

    receive_message();
    Serial.printf("Java server responded: %s\n", incomingPacket);

    if (strcmp(incomingPacket, "STOPC") == 0) {
      send_message("Acting on STOPC", javaServerIP, javaServerPort);
    } else if (strcmp(incomingPacket, "STOPO") == 0) {
      send_message("Acting on STOPO", javaServerIP, javaServerPort);
    } else if (strcmp(incomingPacket, "FSLOWC") == 0) {
      send_message("Acting on FSLOWC", javaServerIP, javaServerPort);
    } else if (strcmp(incomingPacket, "FFASTC") == 0) {
      send_message("Acting on FFASTC", javaServerIP, javaServerPort);
    } else if (strcmp(incomingPacket, "RSLOWC") == 0) {
      send_message("Acting on RSLOWC", javaServerIP, javaServerPort);
    } else if (strcmp(incomingPacket, "DISCONNECT") == 0) {
      send_message("Acting on DISCONNECT", javaServerIP, javaServerPort);
      state = 2;
      break;
    }
    
    // Check if needs to open doors only when stopped
    if (move_state == 0) {
      if (incomingPacket == "OPEN_DOOR") {
        if (door_state == 0) {
          Serial.println("Opening Door");
          open_door();
        }
      } else if (incomingPacket == "CLOSE_DOOR") {
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
