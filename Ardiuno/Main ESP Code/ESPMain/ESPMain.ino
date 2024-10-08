#define motor_direction 23
#define motor_speed 22
#define green_led 18
#define yellow_led 19
#define red_led 21
#define ir_sensor 30 // CHANGE
#define servo 31     // CHANGE
#define trigger 25
#define echo 26

#include <WiFi.h>
#include <WiFiUdp.h>

WiFiUDP udp;

const char *ssid = "WIFI NAME";
const char *password = "WIFI PASSWORD";
const unsigned int localUdpPort = 4210; // Local port to listen on
char incomingPacket[255];               // Buffer for incoming packets

IPAddress javaServerIP(192, 168, 0, 37);
unsigned int javaServerPort = 4210;

char door_status[10] = "CLOSED";
char move_status[10] = "STOPPED";

int state;
int current_speed;
int door_state;
int move_state;
int sensor_status;
int safeDisconnect;

void setup()
{
  Serial.begin(115200);
  Serial.println();

  state = 0;
  current_speed = 0;
  door_state = 0;
  move_state = 0;
  safeDisconnect = 0;

  // Connect to Wi-Fi
  Serial.printf("Connecting to %s ", ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED)
  {
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
  pinMode(trigger, OUTPUT);
  pinMode(echo, INPUT);
  digitalWrite(motor_direction,HIGH);
}

void loop()
{
  long duration, distance;
  // Initialise connection with wifi packets
  while (state == 0)
  {
    Serial.println("Initialising connection with ESP and Java Server");

    send_message("(INIT) from ESP", javaServerIP, javaServerPort);
    delay(1000);

    receive_message();

    if (strcmp(incomingPacket, "(INIT Confirmed) from CCP") == 0)
    {
      // send_message("Initialising packet successful (FROM ESP32)", javaServerIP, javaServerPort);
      state = 1;
    }
  }

  // Main loop
  while (state == 1)
  {

    receive_message();

    send_message("STOPC", javaServerIP, javaServerPort);
    delay(1000);

    receive_message();
    Serial.printf("Java server responded: %s\n", incomingPacket);

    detect();
    // Collision Detection
    digitalWrite(trigger, LOW);
    delayMicroseconds(2);
    digitalWrite(trigger, HIGH);
    delayMicroseconds(10);
    digitalWrite(trigger, LOW);
    duration = pulseIn(echo, HIGH);
    distance = (((duration / 2) / 29.1) * 10);

    if (distance < 100 && distance > 0)
    {
      stop_br();
      close_door();
          send_message("STOPC", javaServerIP, javaServerPort); // read in the MCP document if we do emergency stop to send STOPC
    }

    // BR should stop and close doors
    if (strcmp(incomingPacket, "STOPC") == 0)
    {
      stop_br();
      close_door();
      delay(1000); // Could be bad to have this delay but how do we know when it has
                   // completely stopped?
      send_message("STOPC", javaServerIP, javaServerPort);
    }
    // BR shuold stop and open doors
    else if (strcmp(incomingPacket, "STOPO") == 0)
    {
      stop_br();
      open_door();
      send_message("STOPO", javaServerIP, javaServerPort);
    }
    // BR should move forward slowly and stop
    // after it has aligned itself with the IR
    // photodiode at a checkpoint or station.
    // BR doors are to remain closed. If the
    // BR is already aligned with an IR
    // photodiode, the BR should not move.
    else if (strcmp(incomingPacket, "FSLOWC") == 0)
    {
      slow_br();
      // do something with IR sensor to detect
      // once detected, stop
      while(sensor_status < 700)
      {
        // while loop used to block stop from excuting until the sensor reads the red LED
      }
    stop_br();
      send_message("STOPC", javaServerIP, javaServerPort);
    }
    // BR moving in forward direction at fast speed
    // and door is closed
    else if (strcmp(incomingPacket, "FFASTC") == 0)
    {
      fast_br();
      if (door_state == 1)
        close_door();
      send_message("FFASTC", javaServerIP, javaServerPort);
    }
    // BR move backwards slowly and stop after it
    // is aligned with IR photodiode at checkpoint
    // Br doors are to remain closed. If BR
    // is already aligned with IR photodiode
    // BR should not move
    else if (strcmp(incomingPacket, "RSLOWC") == 0)
    {
      // need move backwards command
      slowReverse_br();

      while(sensor_status < 700)
      {
         // while loop used to block stop from excuting until the sensor reads the red LED
      }
      stop_br();
      digitalWrite(motor_direction, HIGH);
      // Once has achieved moving slow and then stopped with doors closed, then send STOPC back
      send_message("STOPC", javaServerIP, javaServerPort);
    }
    // BR status LED should flash at 2 HZ to
    // indicate that it is to be removed from track
    else if (strcmp(incomingPacket, "DISCONNECT") == 0)
    {
      send_message("OFLN", javaServerIP, javaServerPort);
      safeDisconnect = 1;
      state = 2;
      break;
    }

    delay(500);
  }

  while (safeDisconnect == 1)
  {
    // flashes twice a second to let us know to disconnect.
    digitalWrite(green_led, LOW);
    digitalWrite(yellow_led, LOW);
    digitalWrite(red_led, LOW);
    delay(250);
    digitalWrite(green_led, HIGH);
    digitalWrite(yellow_led, HIGH);
    digitalWrite(red_led, HIGH);
    delay(250);
  }
}

/* Wifi Code */
void send_message(const char *str, IPAddress remoteIP, unsigned int remotePort)
{
  udp.beginPacket(remoteIP, remotePort);
  udp.write((uint8_t *)str, strlen(str));
  udp.endPacket();
  Serial.printf("Sent a UDP packet to %s, port %d\n", remoteIP.toString().c_str(), remotePort);
}
void receive_message()
{
  int packetSize = udp.parsePacket();
  if (packetSize)
  {
    int len = udp.read(incomingPacket, 255);
    if (len > 0)
    {
      incomingPacket[len] = 0;
    }
    Serial.printf("Received %d bytes from %s, port %d\n", packetSize, udp.remoteIP().toString().c_str(), udp.remotePort());
    Serial.printf("UDP packet contents: %s\n", incomingPacket);
  }
}
char *get_status()
{
  // static char message[50];
  // snprintf(message, sizeof(message), "(STAT) Door: %s Moving: %s", door_status, move_status);
  char *message = "STOPC";
  return message;
}

/* Motor Code */

void slow_br()
{
  move_state = 1;
  for (int i = current_speed; i >= 100; i -= 5)
  { // slowdown
    analogWrite(motor_speed, i);
    delay(100);
  }
  current_speed = 100;
}
void stop_br()
{
  move_state = 0;
  analogWrite(motor_speed, 0);
  current_speed = 0;
}
void fast_br()
{
  move_state = 2;
  for (int i = current_speed; i <= 255; i += 5)
  { // speed up
    analogWrite(motor_speed, i);
    delay(100);
  }
  current_speed = 255;
}

void slowReverse_br()
{
  digitalWrite(motor_direction, LOW); // reverse direction
  move_state = 3;
  analogWrite(motor_speed, 50);
  current_speed = 50;
}

/* Servo Code */
void open_door()
{
  digitalWrite(servo, HIGH);
  door_state = 1;
}
void close_door()
{
  digitalWrite(servo, LOW);
  door_state = 0;
}

/* IR Sensor Code */
void detect()
{
  sensor_status = analogRead(ir_sensor);
}
