#include "Arduino.h"
#include "Servo_Controller.h"

Servo_Controller::Servo_Controller(int pin1, int doorState) {
  pinMode(pin1, OUTPUT);

  _servo_pin = pin1;
  _doorState = doorState;
}

void Servo_Controller::testFunction(int test) {
  Serial.println(test);
}

int Servo_Controller::getDoorState() {
  return _doorState;
}

void Servo_Controller::openDoor() {
  digitalWrite(_servo_pin, HIGH);
  _doorState = 1;
}

void Servo_Controller::closeDoor() {
  digitalWrite(_servo_pin, LOW);
  _doorState = 0;
}