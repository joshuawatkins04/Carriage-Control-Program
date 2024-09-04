#ifndef Servo_Controller_h
#define Servo_Controller_h

#include "Arduino.h"

class Servo_Controller {
  
  private:

    int _servo_pin;
    int _doorState;

  public:

    IR_Sensor(int pin1);

    void testFunction(int test);
    int getDoorState();
    void openDoor();
    void closeDoor();

};

#endif