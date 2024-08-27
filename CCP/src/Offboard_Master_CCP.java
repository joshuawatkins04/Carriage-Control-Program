/*
 * Has the role of connecting to and communicating with 
 * the MCP and the ESP32_Manager program.
 */

public class Offboard_Master_CCP {

    public static void main(String[] args) throws Exception {
        CCP_Connection_Man ccp_connection_man = new CCP_Connection_Man();

        // System.out.println("\n\nINITIALISING PHASE:\n");
        // System.out.println("Status of IR_Sensor: " + ccp_connection_man.get_IS_Status());
        // System.out.println("Status of Motor_Controller: " + ccp_connection_man.get_MC_Status());
        // System.out.println("Status of Servo_Controller: " + ccp_connection_man.get_SC_Status());
        // System.out.println("Carriage Status: " + ccp_connection_man.send_Carriage_Status());
        // System.out.println("Door State: " + ccp_connection_man.get_Door_State());

        // // Commanding to Open door when at station
        // ccp_connection_man.esp32_manager.door_Command("OPEN");
        // System.out.println("\n\nDoor should be set to open...\nState is: " + ccp_connection_man.get_Door_State());
    
        UDPTest test = new UDPTest();
        test.setup();
        test.testSend();
        test.tearDown();
        
    }
}
