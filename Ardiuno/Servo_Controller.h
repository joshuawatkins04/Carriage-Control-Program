#include <iostream>
#include "Setup.h"
#define string std::string
#ifndef SERVO_CONTROLLER_H
#define SERVO_CONTROLLER_H

class Servo_Controller {

    public:
    
    string doorState;
    bool error; // just testing

    Servo_Controller() {
        doorState = "CLOSE";
    }

    bool operational() {
        return true;
    }

    void change_state(string s) {
        if (s == "") {
            doorState = "ERR";
        } else if (s == "OPEN") {
            doorState = "OPEN";
        } else if (s == "CLOSE") {
            doorState = "CLOSE";
        } else {
            doorState = "ERR";
        }
    }
};

#endif