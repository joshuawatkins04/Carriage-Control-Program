#include "Arduino.h"
#include "Motor_Controller.h"

Motor_Controller::Motor_Controller(int pin1, int pin2) {
  pinMode(pin1, OUTPUT);
  pinMode(pin2, OUTPUT);

  _motor_direction_pin = pin1;
  _motor_speed_pin = pin2;
}

void Motor_Controller::testFunction(int test) {
  Serial.println(test);
}

void Motor_Controller::moveBR(int setSpeed) {
  if (setSpeed > 255) setSpeed = 255;  // Cap speed to 255
  if (setSpeed < 0) setSpeed = 0;      // Minimum speed

  analogWrite(_motor_speed_pin, setSpeed);  // Set speed using PWM
  digitalWrite(_motor_direction_pin, HIGH);  // Set direction to forward

  current_speed = setSpeed;  //current speed
}

void Motor_Controller::stopBR() {
  analogWrite(_motor_speed_pin, 0);  // Stop motor 
  current_speed = 0;
}

void Motor_Controller::slowBR() {
  for (int i = current_speed; i >= 100; i -= 5) { //slowdown
    analogWrite(_motor_speed_pin, i);
    delay(50);  
  }
  current_speed = 100;
}

void Motor_Controller::fastBR() {
  for (int i = current_speed; i <= 255; i += 5) {  //speed up 
    analogWrite(_motor_speed_pin, i);
    delay(50);
  }
  current_speed = 255;
}