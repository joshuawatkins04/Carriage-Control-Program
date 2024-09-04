#define motor_direction 16
#define motor_speed 17
#define servo 20
#define ir_sensor 21
#define green_led 26
#define yellow_led 27
#define red_led 28
//#define LED 21

#include <Motor_Controller.h>
#include <IR_Sensor.h>
#include <Servo_Controller.h>

// Initialise Classes and assigns pins to each
Motor_Controller mc(motor_direction, motor_speed);
IR_Sensor ir(ir_sensor);
Servo_Controller sc(servo);

// Setup
void setup() {
  Serial.begin(115200);
}

// Main loop
void loop() {
  
  // Test Motor
  Serial.println("\n\n\n\nTesting Motor Controller");
  Serial.println("Starting Motor");
  mc.moveBR(0); // need to test
  delay(3000);
  mc.stopBR();
  Serial.println("Stopping motor");
  delay(3000);

  // Test IR_Sensor
  Serial.println("\n\n\n\nTesting IR Sensor");
  ir.detect();

  // Test Servo
  Serial.println("\n\n\n\nTesting Servo Motor");
  Serial.println("Door state is: ");
  Serial.print(sc.getDoorState());
  delay(1000);
  Serial.println("Changing door state to open");
  sc.openDoor();
  Serial.println("Door state is: ");
  Serial.print(sc.getDoorState());
  delay(3000);
  Serial.println("Changing door state to closed");
  sc.closeDoor();
  Serial.println("Door state is: ");
  Serial.print(sc.getDoorState());
  delay(3000);
}
