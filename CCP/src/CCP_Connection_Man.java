/*
 * Needs to connect to the MCP successfully
 * 
 * BUT also needs to receive information from
 * ESP32??? to then provide information to
 * Offboard CCP?
 */

public class CCP_Connection_Man {

    ESP32_Manager esp32_manager;

    public CCP_Connection_Man() {
        esp32_manager = new ESP32_Manager();
    }

    /*
     * Function needs to send directly to MCP
     * This function will be called in Offboard CCP when it needs to send this to MCP.
     */
    String send_Carriage_Status() {
        // needs to write this to the open port thingo in the MCP

        // this return is just here for testing
        return esp32_manager.send_Carriage_Status();
    }

    boolean get_IS_Status() {
        return esp32_manager.get_IS_Status();
    }

    boolean get_MC_Status() {
        return esp32_manager.get_MC_Status();
    }

    boolean get_SC_Status() {
        return esp32_manager.get_SC_Status();
    }

    String get_Door_State() {
        return esp32_manager.get_Door_State();
    }
}
