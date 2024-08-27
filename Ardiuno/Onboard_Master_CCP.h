#include <iostream>
#include "IR_Sensor.h"
#include "Servo_Controller.h"
#include "Motor_Controller.h"

class Onboard_Master_CCP {

    IR_Sensor ir_sensor;
    Servo_Controller servo_controller;
    Motor_Controller motor_controller;
    
    public:
    
    bool IS, SC, MC;

    Onboard_Master_CCP() {
        IS = ir_sensor.operational();
        SC = servo_controller.operational();
        MC = motor_controller.operational();
    }
    
    bool operational() {
        return IS && SC && MC;
    }

    string carriage_status() {
        if (operational()) {
            return "ON";
        } else {
            return "ERR";
        }
    }

    string get_door_state() {
        return servo_controller.doorState;
    }

    void door_command(string s) {
        servo_controller.change_state(s);
    }
};