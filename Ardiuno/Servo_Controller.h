#include <iostream>
#define string std::string

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