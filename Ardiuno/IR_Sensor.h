#include "Setup.h"

class IR_Sensor {
    private:
        int ir_sensor_pin; //change to pin thats being used

    public:

    IR_Sensor() : ir_sensor_pin(Setup::ir_sensor) {
        pinMode(ir_sensor_pin, INPUT);
    }

    bool operational() {
        int sensorState = digitalRead(ir_sensor_pin);
        return sensorState == HIGH;
    }
};

/* IMPLEMENT somewhere idk?
void loop() {
    if(irSensor.operational()){
        motor.slowBR();
        motor.stopBR();
    } else{
        motor.fastBR();
    }
    delay(500);
}
*/