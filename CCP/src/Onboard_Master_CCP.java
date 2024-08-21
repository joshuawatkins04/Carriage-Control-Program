/*
 * Takes in outputs from the Servo_Controller
 * IR_Sensor and Servo_Controller and handles
 * this information to then send it to the ESP32
 * Manager and then the Offboard CCP to the MCP.
 */

public class Onboard_Master_CCP {
    
    IR_Sensor ir_sensor;
    Servo_Controller servo_controller;
    Motor_Controller motor_controller;
    public boolean IS, SC, MC;

    public Onboard_Master_CCP() {
        ir_sensor = new IR_Sensor();
        servo_controller = new Servo_Controller();
        motor_controller = new Motor_Controller();
        IS = ir_sensor.operational();
        SC = servo_controller.operational();
        MC = motor_controller.operational();
    }

    /*
     * Determines whether all components of
     * bladerunner are operational, then returns
     * this information to carriageStatus()
     * to then communicate this to the MCP.
     */
    boolean operational() {
        return IS && SC && MC;
    }

    /*
     * Communicates with MCP to tell the program
     * the status of the carriage
     */
    String carriage_Status() {
        if (operational()) {
            return "ON";
        } else {
            return "ERR";
        }
    }

    String get_Door_State() {
        return servo_controller.doorState;
    }

    void door_Command(String s) {
        servo_controller.change_State(s);
    }
}
