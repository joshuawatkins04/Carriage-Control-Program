#ifndef IR_Sensor_h
#define IR_Sensor_h

#include "Arduino.h"

class IR_Sensor {
  
  private:

    int _ir_sensor_pin;
    //int _led_pin;

  public:

    IR_Sensor(int pin1);

    void testFunction(int test);
    void detect();

};

#endif