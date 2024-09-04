#include "Arduino.h"
#include "Servo_Controller.h"

int doorState;

Servo_Controller::Servo_Controller(int pin1) {
  pinMode(pin1, OUTPUT);

  _servo_pin = pin1;
  doorState = 0;
}

void Servo_Controller::testFunction(int test) {
  Serial.println(test);
}

int Servo_Controller::getDoorState() {
  return doorState;
}

void Servo_Controller::openDoor() {
  digitalWrite(_servo_pin, HIGH);
  doorState = 1;
}

void Servo_Controller::closeDoor() {
  digitalWrite(_servo_pin, LOW);
  doorState = 0;
}