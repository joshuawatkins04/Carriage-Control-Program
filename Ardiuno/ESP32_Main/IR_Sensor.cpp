#include "Arduino.h"
#include "IR_Sensor.h"

IR_Sensor::IR_Sensor(int pin1) {
  pinMode(pin1, INPUT);
  //pinMode(pin2, OUTPUT);

  _ir_sensor_pin = pin1;
  //_led_pin = pin2;
}

void IR_Sensor::testFunction(int test) {
  Serial.println(test);
}

void IR_Sensor::detect() {
  int SensorStatus = digitalRead(_ir_sensor_pin);

  if (SensorStatus == 1) {
    //digitalWrite(_led_pin, LOW);
    //Serial.println("Motion Ended!");
  } else {
    //digitalWrite(_led_pin, HIGH);
    Serial.println("Motion Detected!");
  }
}