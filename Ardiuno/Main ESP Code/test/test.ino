#define motor_direction 23
#define motor_speed 22
#define green_led 5
#define yellow_led 18
#define red_led 19
// #define green_led 18
// #define yellow_led 19
// #define red_led 21
#define ir_sensor 33
#define trigger 25
#define echo 26
#define servo 32


int state;
int currentSpeed;
int doorState;
int sensorStatus;
int safeDisconnect;

void setup()
{
  Serial.begin(115200);
  Serial.println();

  state = 1;
  currentSpeed = 0;
  doorState = 0;
  safeDisconnect = 0;

  pinMode(motor_speed, OUTPUT);
  pinMode(motor_direction, OUTPUT);
  pinMode(green_led, OUTPUT);
  pinMode(yellow_led, OUTPUT);
  pinMode(red_led, OUTPUT);
  digitalWrite(green_led, LOW);
  digitalWrite(yellow_led, LOW);
  digitalWrite(red_led, LOW);
  digitalWrite(motor_direction,HIGH);
}

int count = 0;

void loop()
{

  // delay(2000);

  // for (int i = 0; i < 2; i++) {
  //   test_servo();
  //   // stopBr();
  //   // test_motor();
  // }
  delay(3000);

  Serial.println("HIGH");
  digitalWrite(motor_direction,HIGH);
  analogWrite(motor_speed, 50);
  delay(5000);
  Serial.println("LOW");
  digitalWrite(motor_direction,LOW);
  delay(5000);

  // test_motor();




  // for (int i = 0; i < 15; i++) {
  //   // flashes twice a second to let us know to disconnect.
  //   Serial.println("Flashing lights");
  //   digitalWrite(green_led, LOW);
  //   digitalWrite(yellow_led, LOW);
  //   digitalWrite(red_led, LOW);
  //   delay(250);
  //   digitalWrite(green_led, HIGH);
  //   digitalWrite(yellow_led, HIGH);
  //   digitalWrite(red_led, HIGH);
  //   delay(250);
  // }

}

void test_servo() {

  openDoor();
  Serial.println("Door Open");
  delay(3000);
  closeDoor();
  Serial.println("Door Closed");
  delay(3000);

}

void test_motor() {

  Serial.println("STOPPED");
  stopBr();
  delay(5000);
  Serial.println("SLOW");
  slowBr();
  delay(3000);
  Serial.println("FAST");
  fastBr();
  delay(3000);
  Serial.println("STOP");
  stopBr();
  delay(3000);
  Serial.println("SLOW REVERSE");
  slowReverseBr();

}



/* Door Code */
void openDoor() {
  digitalWrite(servo, HIGH);
  doorState = 1;
}

void closeDoor() {
  digitalWrite(servo, LOW);
  doorState = 0;
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
