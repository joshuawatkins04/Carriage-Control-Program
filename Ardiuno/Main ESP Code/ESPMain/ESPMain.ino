#define motor_direction 23
#define motor_speed 22
#define green_led 18
#define yellow_led 19
#define red_led 21
// #define ir_sensor 
// #define servo 

void setup() {
    Serial.begin(115200);

    pinMode(motor_speed, OUTPUT);
    pinMode(motor_direction, OUTPUT);
    pinMode(green_led, OUTPUT);
    pinMode(yellow_led, OUTPUT);
    pinMode(red_led, OUTPUT);
    //pinMode(ir_sensor, INPUT);
    //pinMode(servo, OUTPUT);
    digitalWrite(green_led, LOW);
    digitalWrite(yellow_led, LOW);
    digitalWrite(red_led, LOW);
}

void loop() {
    
}