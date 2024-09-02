#include <iostream>
#include "IR_Sensor.h"
#include "Servo_Controller.h"
#include "Motor_Controller.h"
#include "ESP32_Manager.h"
#include "Setup.h"
#include <thread>

class Onboard_Master_CCP
{

    IR_Sensor ir_sensor;
    Servo_Controller servo_controller;
    Motor_Controller motor_controller;

private:
    static int BladeRunnerState;
    static bool InterruptStatus;
    static string CCPMessageToSend;
    static string CCPMessageRecieved;
    std::thread ThreadConnection;

public:
    static bool IS, SC, MC;

    Onboard_Master_CCP()
    {
        // Initialize Static Varriables
        BladeRunnerState = 0; // 0 == Stopped , 1 == Running 
        InterruptStatus = 0;
        CCPMessageToSend = "";
        CCPMessageRecieved = "";
        IS = ir_sensor.operational();
        SC = servo_controller.operational();
        MC = motor_controller.operational();

        start();

        while (BladeRunnerState != 0)
        {
            if (CCPMessageRecieved == "SlowDown")
            {
            }
            if (CCPMessageRecieved == "SpeedUp")
            {
            }
            if (CCPMessageRecieved == "Stop")
            {
            }
            if (CCPMessageRecieved == "OpenDoor")
            {
            }
            if (CCPMessageRecieved == "CloseDoor")
            {
            }
            // etc
        }
    }

    void interrupt()
    {
        InterruptStatus = true;
    }

    void start()
    {
        // Stopped State CCPState == 0
        if (BladeRunnerState == 0)
        {
            Setup::setup(); // calls the setup does not create an instance of it
            ThreadConnection = std::thread(ThreadConnectionFoo, this);
            BladeRunnerState = 1; // Running
        }
        else
        {
            printf("Error: Failed Start - Can only start from Off State\n");
        }
    }

    void ThreadConnectionFoo()
    {
        ESP32_Manager connectionManagerESP = new ESP32_Manager;
        const auto sleep_time = std::chrono::milliseconds(3000);
        while (BladeRunnerState != 0)
        {
            std::cout << id << std::flush;
            for (int i = 0; i < 3; i++)
            {
                if (CCPMessageToSend != "")
                {
                    connectionManagerESP.send_at_station();
                    connectionManagerESP.receive_message(); // This needs Visibility to give me the ability to set CCPMessageRecieved
                }
                else
                {
                    connectionManagerESP.send_status();
                    connectionManagerESP.receive_message(); // This needs Visibility to give me the ability to set CCPMessageRecieved
                }
                if (CCPMessageRecieved == "Confirm")
                {
                    CCPMessageToSend = "";
                }
                std::this_thread::sleep_for(sleep_time);
            }
        }
        std::cout << id << "Closing ESP Connection Thread\n"
                  << std::flush;
    }

    void stop()
    {
        // Running State CCPState == 1
        if (BladeRunnerState == 1)
        {
            ThreadConnection.exit();
            BladeRunnerState = 0; // Stopped
        }
        else
        {
            printf("Error: Failed Stop - Can only start from Running State\n");
        }
    }

    bool operational()
    {
        return IS && SC && MC;
    }

    string carriage_status()
    {
        if (operational())
        {
            return "ON";
        }
        else
        {
            return "ERR";
        }
    }

    string get_door_state()
    {
        return servo_controller.doorState;
    }

    void door_command(string s)
    {
        servo_controller.change_state(s);
    }
};
