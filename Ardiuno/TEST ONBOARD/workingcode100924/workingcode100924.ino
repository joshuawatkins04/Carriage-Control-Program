#define motor_direction 23
#define motor_speed 22
#define green_led 18
#define yellow_led 19
#define red_led 21
//#define ir_sensor 
//#define servo

void setup() {
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
}

void loop() {
  //motor_test();
  //led_test();
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