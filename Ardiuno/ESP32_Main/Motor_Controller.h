#ifndef MotorController_h
#define MotorController_h

#include "Arduino.h"

class Motor_Controller {
  
  private:

    int _motor_direction_pin;
    int _motor_speed_pin;
    int _current_speed;

  public:

    Motor_Controller(int pin1, int pin2);

    void testFunction(int test);
    void moveBR(int setSpeed);
    void stopBR();
    void slowBR();
    void fastBR();

};

#endif