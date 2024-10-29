#define motor_direction 23
#define motor_speed 22
#define green_led 5 // 18
#define yellow_led 18 // 19
#define red_led 19 // 21
#define ir_sensor 33
#define trigger 25
#define echo 26

#include <WiFi.h>
#include <WiFiUdp.h>

WiFiUDP udp;

const char *ssid = "ENGG2K3K";
const unsigned int localUdpPort = 4210; // Local port to listen on
char incomingPacket[255];               // Buffer for incoming packets

IPAddress javaServerIP(10, 20, 30, 147); // Or could be 10, 20, 30, 142 or 115
unsigned int javaServerPort = 3015;

// IPAddress local_ip(10, 20, 30, 115);


int state;
int currentSpeed;
int sensorStatus;
int safeDisconnect;

void setup()
{
  Serial.begin(115200);
  Serial.println();

  state = 0;
  currentSpeed = 0;
  safeDisconnect = 0;

  // Connect to Wi-Fi
  Serial.printf("Connecting to %s ", ssid);
  WiFi.begin(ssid);
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  // WiFi.config(local_ip);
  Serial.println(" connected");

  // Start listening for UDP packets
  udp.begin(localUdpPort);
  Serial.printf("Now listening at IP %s, UDP port %d\n", WiFi.localIP().toString().c_str(), localUdpPort);
  //Serial.printf("Now listening at IP %s, UDP port %d\n", local_ip, localUdpPort);

  pinMode(motor_speed, OUTPUT);
  pinMode(motor_direction, OUTPUT);
  pinMode(green_led, OUTPUT);
  pinMode(yellow_led, OUTPUT);
  pinMode(red_led, OUTPUT);
  pinMode(ir_sensor, INPUT);
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

    sendMessage("EXEC_INIT", javaServerIP, javaServerPort);
    delay(1000);

    receiveMessage();

    if (strcmp(incomingPacket, "INIT_CONF") == 0)
    {
      sendMessage("STOPC", javaServerIP, javaServerPort); // Needs to send actual status of BR
      state = 1;
    }
  }

  // Main loop
  while (state == 1)
  {

    receiveMessage();


    delay(1000);

    receiveMessage();
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

    if (distance < 100 && distance > 0) {
      stopBr();
      sendMessage("STOPC", javaServerIP, javaServerPort); // read in the MCP document if we do emergency stop to send STOPC
    }

    // BR should stop and close doors
    if (strcmp(incomingPacket, "STOPC") == 0)
    {
      stopBr();
      delay(1000); // Could be bad to have this delay but how do we know when it has
                   // completely stopped?
      sendMessage("STOPC", javaServerIP, javaServerPort);
    }
    // BR shuold stop and open doors
    else if (strcmp(incomingPacket, "STOPO") == 0)
    {
      stopBr();
      sendMessage("STOPO", javaServerIP, javaServerPort);
    }
    // BR should move forward slowly and stop
    // after it has aligned itself with the IR
    // photodiode at a checkpoint or station.
    // BR doors are to remain closed. If the
    // BR is already aligned with an IR
    // photodiode, the BR should not move.
    else if (strcmp(incomingPacket, "FSLOWC") == 0)
    {
      slowBr();
      // do something with IR sensor to detect
      // once detected, stop
      while(sensorStatus < 700)
      {
        // while loop used to block stop from excuting until the sensor reads the red LED
      }
    stopBr();
      sendMessage("STOPC", javaServerIP, javaServerPort);
    }
    // BR moving in forward direction at fast speed
    // and door is closed
    else if (strcmp(incomingPacket, "FFASTC") == 0)
    {
      fastBr();
      sendMessage("FFASTC", javaServerIP, javaServerPort);
    }
    // BR move backwards slowly and stop after it
    // is aligned with IR photodiode at checkpoint
    // Br doors are to remain closed. If BR
    // is already aligned with IR photodiode
    // BR should not move
    else if (strcmp(incomingPacket, "RSLOWC") == 0)
    {
      // need move backwards command
      slowReverseBr();

      while(sensorStatus < 700)
      {
         // while loop used to block stop from excuting until the sensor reads the red LED
      }
      stopBr();
      digitalWrite(motor_direction, HIGH);
      // Once has achieved moving slow and then stopped with doors closed, then send STOPC back
      sendMessage("STOPC", javaServerIP, javaServerPort);
    }
    // BR status LED should flash at 2 HZ to
    // indicate that it is to be removed from track
    else if (strcmp(incomingPacket, "DISCONNECT") == 0)
    {
      sendMessage("OFLN", javaServerIP, javaServerPort);
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
void sendMessage(const char *str, IPAddress remoteIP, unsigned int remotePort)
{
  udp.beginPacket(remoteIP, remotePort);
  udp.write((uint8_t *)str, strlen(str));
  udp.endPacket();
  Serial.printf("Sent a UDP packet to %s, port %d\n", remoteIP.toString().c_str(), remotePort);
}
void receiveMessage()
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
char *getStatus()
{
  // static char message[50];
  // snprintf(message, sizeof(message), "(STAT) Door: %s Moving: %s", door_status, move_status);
  char *message = "STOPC";
  return message;
}

/* Motor Code */

void slowBr()
{
  for (int i = currentSpeed; i >= 100; i -= 5)
  { // slowdown
    analogWrite(motor_speed, i);
    delay(100);
  }
  currentSpeed = 100;
}
void stopBr()
{
  analogWrite(motor_speed, 0);
  currentSpeed = 0;
}
void fastBr()
{
  for (int i = currentSpeed; i <= 255; i += 5)
  { // speed up
    analogWrite(motor_speed, i);
    delay(100);
  }
  currentSpeed = 255;
}

void slowReverseBr()
{
  digitalWrite(motor_direction, LOW); // reverse direction
  analogWrite(motor_speed, 50);
  currentSpeed = 50;
}

/* IR Sensor Code */
void detect()
{
  sensorStatus = analogRead(ir_sensor);
}
