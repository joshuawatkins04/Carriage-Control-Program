#define direction 23
#define speed 22
#define red 21
#define green 18
#define yellow 19
//#define IR 
//#define servo

void setup() {
pinMode(speed,OUTPUT);
pinMode(direction,OUTPUT);
pinMode(red,OUTPUT);
pinMode(green,OUTPUT);
pinMode(yellow,OUTPUT);
//pinMode(IR,INPUT);
//pinMode(servo,OUTPUT);
digitalWrite(red,LOW);
digitalWrite(green,LOW);
digitalWrite(yellow,LOW);
}

void loop() {

//motor_test();
//LED_test();
//servo_test();
//ir_test();
}

void LED_test()
{
digitalWrite(red,HIGH);
digitalWrite(green,HIGH);
digitalWrite(yellow,HIGH);
delay(1000);
digitalWrite(red,LOW);
digitalWrite(green,LOW);
digitalWrite(yellow,LOW);
delay(1000);
}

void ir_test()
{
switch(digitalRead(IR))
case >100:
   Serial.println("THERE IS LIGHT");
    break;
 case <100:
  Serial.println("NO LIGHT");
    break;


}

void servo_test()
{
  delay(2000);
  digitalWrite(servo,HIGH);
  Serial.println("open");
  delay(2000);
  digitalWrite(servo,LOW);
   Serial.println("closed");

}
void motor_test()
{
digitalWrite(direction,HIGH);
analogWrite(speed,255);
delay(2000);
analogWrite(speed,0);
delay(2000);

digitalWrite(direction,LOW);
analogWrite(speed,255);
delay(2000);
}