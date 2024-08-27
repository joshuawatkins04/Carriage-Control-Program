#include <iostream>
#include <string>
#include "Onboard_Master_CCP.h"

class ESP32_Manager {

    Onboard_Master_CCP onboard_master_ccp;

    public:

    ESP32_Manager() {

    }
    
    string send_carriage_status() {
        return onboard_master_ccp.carriage_status();
    }

    bool get_IS_status() {
        return onboard_master_ccp.IS;
    }

    bool get_MC_status() {
        return onboard_master_ccp.MC;
    }

    bool get_SC_status() {
        return onboard_master_ccp.SC;
    }

    string get_door_state() {
        return onboard_master_ccp.get_door_state();
    }

    void door_command(string s) {
        onboard_master_ccp.door_command(s);
    }
};