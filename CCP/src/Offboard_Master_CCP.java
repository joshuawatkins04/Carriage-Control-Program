/*
 * Has the role of connecting to and communicating with 
 * the MCP and the ESP32_Manager program.
 */

public class Offboard_Master_CCP {

    public static void main(String[] args) throws Exception {
        CCP_Connection_Man ccp_connection_man = new CCP_Connection_Man();
    
        ccp_connection_man.setup();
        ccp_connection_man.send_to_MCP("Test");
        ccp_connection_man.end_connection();

        // UDPTest test = new UDPTest();
        // test.setup();
        // test.testSend();
        // test.tearDown();
        
    }
}
