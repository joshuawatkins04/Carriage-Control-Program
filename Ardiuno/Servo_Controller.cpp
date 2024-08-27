/* 

THIS IS THE OLD JAVA CODE

CHANGE TO C++


 * Controls the doors of the Carriage and
 * also sends back information on the state
 * of the doors.
 

public class Servo_Controller {

    public String doorState;
    public boolean error; // just testing

    public Servo_Controller() {
        doorState = "CLOSE";
        error = false;
    }

    
     * Used to determine if whole carriage is
     * operational
     
    boolean operational() {
        // do something to determine
        return true;
    }

    
     * Do something to open the door and also
     * change the doorState to OPEN.
     

    void change_State(String s) {
        if (null == s) {
            doorState = "ERR";
        } else doorState = switch (s) {
            case "OPEN" -> "OPEN";
            case "CLOSE" -> "CLOSE";
            default -> "ERR";
        };
    }
}


*/

#include <iostream>
#include <string>
