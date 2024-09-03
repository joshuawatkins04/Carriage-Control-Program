#include "Setup.h"

#ifndef MOTOR_CONTROLLER_H
#define MOTOR_CONTROLLER_H

class Motor_Controller {
    private:
        int motor_speed_pin; // pwm pin connected
        int motor_direction_pin;// direction pin connected
        int current_speed; //current speed

    public:
    
     Motor_Controller() 
        : motor_speed_pin(Setup::motor_speed), motor_direction_pin(Setup::motor_direction), current_speed(0) {
        pinMode(motor_speed_pin, OUTPUT);
        pinMode(motor_direction_pin, OUTPUT);
        stopBR();  // Initially stop motor
    }

    void moveBR(int setSpeed) {
        if (setSpeed > 255) setSpeed = 255;  // Cap speed to 255
        if (setSpeed < 0) setSpeed = 0;      // Minimum speed

        analogWrite(motor_speed_pin, setSpeed);  // Set speed using PWM
        digitalWrite(motor_direction_pin, HIGH);  // Set direction to forward

        current_speed = setSpeed;  //current speed
    }

    void stopBR() {
        analogWrite(motor_speed_pin, 0);  // Stop motor 
        current_speed = 0;
    }

    void slowBR() {
        for (int i = current_speed; i >= 100; i -= 5) { //slowdown
            analogWrite(motor_speed_pin, i);
            delay(50);  
        }
        current_speed = 100;
    }

    void fastBR() {
        for (int i = current_speed; i <= 255; i += 5) {  //speed up 
            analogWrite(motor_speed_pin, i);
            delay(50);
        }
        current_speed = 255;
    }
};

#endif