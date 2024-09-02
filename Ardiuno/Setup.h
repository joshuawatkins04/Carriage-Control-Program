#ifndef SETUP_H
#define SETUP_H

class Setup {
public:
    
    static const int motor_direction = 16;
    static const int motor_speed = 17;
    static const int servo = 20;
    static const int ir_sensor = 21;
    static const int green_led = 26;
    static const int yellow_led = 27;
    static const int red_led = 28;

    
    static void setup() {
        pinMode(motor_direction, OUTPUT);
        pinMode(motor_speed, OUTPUT);
        pinMode(servo, OUTPUT);
        pinMode(ir_sensor, INPUT);
        pinMode(green_led, OUTPUT);
        pinMode(yellow_led, OUTPUT);
        pinMode(red_led, OUTPUT);
    }
};

#endif 
