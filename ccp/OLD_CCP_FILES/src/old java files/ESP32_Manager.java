/*
 * Receives signals from the Offboard_Master_CCP
 * and connects to the Onboard_Master_CCP
 
 
 KEEP HERE JUST FOR TESTING PURPOSES JUST IN CASE
 
 */

public class ESP32_Manager {
    
    Onboard_Master_CCP onboard_master_ccp;

    public ESP32_Manager() {
        onboard_master_ccp = new Onboard_Master_CCP();
    }

    String send_Carriage_Status() {
        return onboard_master_ccp.carriage_Status();
    }

    boolean get_IS_Status() {
        return onboard_master_ccp.IS;
    }

    boolean get_MC_Status() {
        return onboard_master_ccp.MC;
    }

    boolean get_SC_Status() {
        return onboard_master_ccp.SC;
    }

    String get_Door_State() {
        return onboard_master_ccp.get_Door_State();
    }

    void door_Command(String s) {
        onboard_master_ccp.door_Command(s);
    }
}
