#define motor_direction 23
#define motor_speed 22
#define green_led 18
#define yellow_led 19
#define red_led 21
//#define ir_sensor 
//#define servo

// #include <WiFi.h>
// #include <WiFiUdp.h>

// WiFiUDP udp;

// const char* ssid = "ENGG2K3K";

// IPAddress ip(10, 20, 30, 115);
// IPAddress udpAddress(10, 20, 30, 1);
// const int udpLocalPort = 3015;
// const int udpAddressPort = 3016;

// IPAddress dns(192, 168, 43, 1);
// IPAddress gateway(192, 168, 43, 1);
// IPAddress subnet(255, 255, 255, 0);

// char inBuffer[255];
// char outBuffer[255];

void setup() {
  Serial.begin(115200);
  
  pinMode(motor_speed, OUTPUT);
  pinMode(motor_direction, OUTPUT);
  pinMode(green_led, OUTPUT);
  pinMode(yellow_led, OUTPUT);
  pinMode(red_led, OUTPUT);
  //pinMode(ir_sensor, INPUT);
  //pinMode(servo, OUTPUT);
  digitalWrite(green_led, LOW);
  digitalWrite(yellow_led, LOW);
  digitalWrite(red_led, LOW);

//   delay(1000);
  
//   WiFi.config(ip, gateway, subnet, dns);
//   WiFi.begin(ssid);
//   Serial.println("\nConnecting");
  
//   while(WiFi.status() != WL_CONNECTED){
//       Serial.print(".");
//       delay(100);
//   }
  
//   Serial.println("\nConnected to the WiFi network");
//   Serial.print("[+] ESP32 IP : ");
//   Serial.println(WiFi.localIP());
  
//   udp.begin(udpLocalPort);
}

void loop() {
  Serial.println("\nBeginning main loop");
  
  motor_test();
  led_test();
  //servo_test();
  //ir_test();
}

void led_test() {
  Serial.println("\nRunning LED Test:");
  digitalWrite(green_led,HIGH);
  digitalWrite(yellow_led,HIGH);
  digitalWrite(red_led,HIGH);
  delay(1000);
  digitalWrite(green_led,LOW);
  digitalWrite(yellow_led,LOW);
  digitalWrite(red_led,LOW);
  delay(1000);
}

// void ir_test() {
//   Serial.println("\nRunning IR Test:");
//   switch(digitalRead(ir_sensor))
//     case > 100:
//       Serial.println("THERE IS LIGHT");
//       break;
//     case <100:
//       Serial.println("NO LIGHT");
//       break;
// }

// void servo_test() {
//   Serial.println("\nRunning Servo Test:");
//   delay(2000);
//   digitalWrite(servo, HIGH);
//   Serial.println("Open door");
//   delay(2000);
//   digitalWrite(servo, LOW);
//   Serial.println("Close door");
// }

void motor_test() {
  Serial.println("\nRunning Motor Test:");
  digitalWrite(motor_direction, HIGH);
  analogWrite(motor_speed, 255);
  delay(2000);
  analogWrite(motor_speed, 0);
  delay(2000);

  digitalWrite(motor_direction, LOW);
  analogWrite(motor_speed, 255);
  delay(2000);
}

// void sendMessage(char str[255]){
//   strcpy(outBuffer, str);
//   udp.beginPacket(udpAddress, udpAddressPort);
//   udp.write(outBuffer, 11);
//   udp.endPacket();
//   //memset(outBuffer, 0, 50);
// }

// void receiveMessage(){
//   udp.parsePacket();
//   if(udp.read(inBuffer, 255) > 0){
//     Serial.print("Server to client: ");
//     Serial.println((char *)inBuffer);
//     if(inBuffer == "START") {
//       sendMessage("STARTING");
//     }
//     if(inBuffer == "STOP") {
//       sendMessage("STOPPING");
//     }
//   }
// }