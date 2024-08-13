# <ins>Team 3 Comms 3 ENGG2k/3k Carriage Control Program Repo</ins>

## CCP Members: Peter Martin, Joshua Watkins, Connell Ng, Andrew Parker, Hirune, Allen


# Protocol Specification Document

## MCP Members: Vikil Chandrapati, Eugene Tshien, Thomas Mikic, Ashton Lane, Krishang, Lardo Terblanche, Alice Nevmerzhytska, Danaan Lawton, Samuel Black, Benjamin Beashel, Muniendra Vasudevan, Keval Gandhi, Shreyas Reddy, Vaibhav Gupta, MD Samuil huga, Rohita Chidiireddi 

**FEEL FREE TO LEAVE COMMENTS**

[**Introduction	2**](\#introduction)

[**Technologies	2**](\#technologies)

[**Components**](\#components)	**5**

[**Assumptions**](\#assumptions)	**5**

[**How It Works**](\#how-it-works)	6

[**Startup Protocol**](\#startup-protocol)	**6**

[**Commands	6**](\#commands)

[Command Structure	6](\#command-structure)

[List of commands	7](\#list-of-commands)

[Commands sent by MCP:	7](\#commands-sent-by-mcp:)

[Commands sent by Client to MCP	8](\#commands-sent-by-client-to-mcp)

[**Inputs/Outputs	9**](\#inputs/outputs)

[Blade Runner	9](\#blade-runner)

[Stations	10](\#stations)

[**Error Handling	10**](\#error-handling)

# Version History

| Version | Date | Changes | Author |
| :---- | :---- | :---- | :---- |
| 0.9 | 31/7/2024 | First draft of interface | The team |
| 1.0 | 7/8/2024 | Formalised interface and incorporated JSON messaging protocol | The team |
| 1.01 | 8/8/2024 | Added clarity to assumptions, how the system will work and the startup protocol \+ formatting  | Thomas |

# Introduction {#introduction}

The purpose of this document is to outline a guideline for interacting with the interface of the Master Control Program (MCP). The MCP’s role involve getting various information from the Carriage Control Programs (CCPs) and Stations such as speed and status to make informed decisions on how to manage various situations around the track, this document is intended for use by anyone involved with the development of the CCP and Stations to provide a foundation to build any client software to interact with this system. Document may be subject to change based on customer requirements.

# Technologies {#technologies}

Programming languages: Java  
Communication Protocol: TCP/IP  
Communication assigned IPs and Ports

| COMPONENT | IP | PORT |
| :---- | :---- | :---- |
| MCP LEDs 1 | 10.20.30.51 | 5001 |
| MCP LEDs 2 | 10.20.30.52 | 5002 |
| MCP LEDs 3 | 10.20.30.53 | 5003 |
| MCP LEDs 4 | 10.20.30.54 | 5004 |
| MCP LEDs 5 | 10.20.30.55 | 5005 |
| MCP LEDs 6 | 10.20.30.56 | 5006 |
| MCP LEDs 7 | 10.20.30.57 | 5007 |
| MCP LEDs 8 | 10.20.30.58 | 5008 |
| MCP LEDs 9 | 10.20.30.59 | 5009 |
| MCP LEDs 10 | 10.20.30.60 | 5010 |
| MCP LEDs 11 | 10.20.30.61 | 5011 |
| MCP LEDs 12 | 10.20.30.62 | 5012 |
| MCP LEDs 13 | 10.20.30.63 | 5013 |
| MCP LEDs 14 | 10.20.30.64 | 5014 |
| MCP LEDs 15 | 10.20.30.65 | 5015 |
| MCP LEDs 16 | 10.20.30.66 | 5016 |
| MCP LEDs 17 | 10.20.30.67 | 5017 |
| MCP LEDs 18 | 10.20.30.68 | 5018 |
| MCP LEDs 19 | 10.20.30.69 | 5019 |
| MCP LEDs 20 | 10.20.30.70 | 5020 |

| COMPONENT | IP | PORT |
| :---- | :---- | :---- |
| BR1 | 10.20.30.101  10.20.30.1 | 3001 |
| BR2 | 10.20.30.102  10.20.30.1 | 3002 |
| BR3 | 10.20.30.103  10.20.30.1 | 3003 |
| BR4 | 10.20.30.104  10.20.30.1 | 3004 |
| BR5 | 10.20.30.105  10.20.30.1 | 3005 |
| BR6 | 10.20.30.106  10.20.30.1 | 3006 |
| BR7 | 10.20.30.107  10.20.30.1 | 3007 |
| BR8 | 10.20.30.108  10.20.30.1 | 3008 |
| BR9 | 10.20.30.109  10.20.30.1 | 3009 |
| BR10 | 10.20.30.110  10.20.30.1 | 3010 |
| BR11 | 10.20.30.111  10.20.30.1 | 3011 |
| BR12 | 10.20.30.112  10.20.30.1 | 3012 |
| BR13 | 10.20.30.113  10.20.30.1 | 3013 |
| BR14 | 10.20.30.114  10.20.30.1 | 3014 |
| BR15 | 10.20.30.115  10.20.30.1 | 3015 |
| BR16 | 10.20.30.116  10.20.30.1 | 3016 |
| BR17 | 10.20.30.117  10.20.30.1 | 3017 |
| BR18 | 10.20.30.118  10.20.30.1 | 3018 |
| BR19 | 10.20.30.119  10.20.30.1 | 3019 |
| BR20 | 10.20.30.120  10.20.30.1 | 3020 |
| BR21 | 10.20.30.121  10.20.30.1 | 3021 |
| BR22 | 10.20.30.122  10.20.30.1 | 3022 |
| BR23 | 10.20.30.123  10.20.30.1 | 3023 |
| BR24 | 10.20.30.124  10.20.30.1 | 3024 |
| BR25 | 10.20.30.125  10.20.30.1 | 3025 |
| BR26 | 10.20.30.126  10.20.30.1 | 3026 |
| BR27 | 10.20.30.127  10.20.30.1 | 3027 |
| BR28 | 10.20.30.128  10.20.30.1 | 3028 |
| BR29 | 10.20.30.129  10.20.30.1 | 3029 |
| BR30 | 10.20.30.130  10.20.30.1 | 3030 |
| BR31 | 10.20.30.131  10.20.30.1 | 3031 |
| BR32 | 10.20.30.132  10.20.30.1 | 3032 |

| COMPONENT | IP | PORT |
| :---- | :---- | :---- |
| Station 1 | 10.20.30.201 | 4001 |
| Station 2 | 10.20.30.202 | 4002 |
| Station 3 | 10.20.30.203 | 4003 |
| Station 4 | 10.20.30.204 | 4004 |
| Station 5 | 10.20.30.205 | 4005 |
| Station 6 | 10.20.30.206 | 4006 |
| Station 7 | 10.20.30.207 | 4007 |
| Station 8 | 10.20.30.208 | 4008 |

# Components {#components}

| Major components | Inner components |
| :---- | :---- |
| Train | Wheels, Motor, Battery, LEDs, Doors(Optional),  |
| Station | Doors(Optional), LED |
| Track | Checkpoint System |
| Checkpoint system | IR Line break sensor, ESP32 |

# Assumptions {#assumptions}

1) There will be a track on which the blade runners will be placed (Whether there will be junctions and turns is undetermined atm)  
2) **The universal direction of forward will be clockwise looking from above**  
3) Along the track are **supports**, which we define as a **checkpoint**. Each **checkpoint** location will never change and can be manually measured and imported into the MCP program. At each **checkpoint an IR line break sensor** are to be placed   
4) Each **checkpoint owns a block of the track** which can be seen in Fig.1 (**The colours are only to represent the different block allocations, there is no colour LEDs or sensors being used**)  
5) **Each train can only be placed in a empty block**  
6) A checkpoint can have a station on it as well, its IR Line Break Sensor will be mapped to the station manually before running the system  
7) Stations cannot move  
8) **Bladerunners will have**  
   **the ability to move forward and backward and slow down**  
9) **Each checkpoint will a IR Line break sensor**   
10) The MCP will import on startup the schedule for the trains  
11) The entire system operates internally operates in cm meaning and distances provided will be in cm and speed will be measured in cm/s as we are assuming not be travelling close to 1 m/s  
12) **There will be max 5 blade runners on the track at a time**  
13) At each station, there will be an IR LED that when turned on will give the emergency stop command to blade runners passing over it \- which will be detected by a IR 	Photoresistor on each blade runner

## How it works {#how-it-works}

IR Line Break Sensors will be placed on every support on the track, when the Blade Runnerpasses by a IR Line break sensor  a signal will be sent by the ESP32 to the MCP signalling a blade runner has passed across the switch. Between each support will be considered a block with only one blade runner in a block at any one time. Allows the MCP to estimate location.

# Startup Protocol {#startup-protocol}

1. MCP turns on  
2. All Checkpoint systems connect to MCP  
3. Set time since startup to 0  
4. All blade runners will be placed in separate blocks  
5. Each blade runner will connect to the MCP  
6. One by one a blade runner is selected (in order of which train connected first), moved forward until it trips a line break. Once it stops at the line break we map the train and its location. Knowing that only one train is moving and only one line break is activated, we can map the train to the location of that individual line break sensor.   
7. This is then repeated for each train, until all train locations are known.  
8. Import the train schedule from an MCP database  
9. The whole system is ready to run normally

# Standard Protocol

* Each blade runner maintains a block gap between other trains  
* If a blade runner enters a block behind another blade runner, it must stop and wait until the blade runner in front has entered the next block to maintain this rule (This will avoid all possible collisions)

# Commands {#commands}

## Command Structure {#command-structure}

**Format**: WIll be sent in JSON commands will be a 4 letter command string.  
**Communication Method**: The MCP is multithreaded system that sends a STATUS message to every carriage every 2 seconds constantly, if a message has not been received by the MCP within 3 seconds of the previous message (to account for any lag) it is safe to assume something an error has occurred on the MCPs end.  
**Data Format**: All speeds will be specified in **cm/s**

## List of commands {#list-of-commands}

### Commands sent by MCP: {#commands-sent-by-mcp:}

| COMMAND | DESCRIPTION |
| :---- | :---- |
| { 	"client\_type": "blade\_runner", 	"message": "AKIN", 	"client\_id": "BRXX", 	"timestamp": 12345, 	"speed": 50, 	"door\_direction": "LEFT/RIGHT",  }  | Acknowledges an init |
| { 	"client\_type": "blade\_runner",   	"message": "status\_retrieve",   	"client\_id": "BRXX",   	"timestamp": 12345 } | Retrieve status sent to train every 2 seconds |
| {   	"client\_type": "blade\_runner",   	"message": "SPED",   	"client\_id": "BRXX",   	"timestamp": 12345,   	"speed": 100 } | Carriage sets speed to value received, speed value can be negative to signal reverse |
| {   	"client\_type": "blade\_runner",   	"message": "KILL",   	"client\_id": "BRXX",   	"timestamp": 12345 } | Tells the train to stop and shut down immediately. Disconnects from the client network and goes speed of 0, stops as fast as it can |

### Commands sent by Client to MCP {#commands-sent-by-client-to-mcp}

| COMMAND | DESCRIPTION |
| :---- | :---- |
| {  	"client\_type": "blade\_runner",  	"message": "MESSAGECODE",  	"client\_id": "BRXX",  	"timestamp": Time since MCP start,  	"status": "ON/OFF/ERR/CRASH/MANSTP",  	"door\_status": "OPEN/CLOSE", 	"connection\_info": {  		"port": "1234",  		"ip": "192.168.0.1"  	}  } | An example of what all the various fields are in the JSON message.  |
| { 	"client\_type": "blade\_runner", 	"message": "BRIN", 	"client\_id": "BR01", }  | First message to begin handshaking for a blade runner. When the client first connects to the MCP it sends us its assigned ID, port and IP so that the MCP can then establish a connection to the client. |
| { 	"client\_type": "station", 	"message": "STIN", 	"client\_id": "ST01", } | First message to begin handshaking for a station. When the client first connects to the MCP it sends us its assigned ID, port and IP so that the MCP can then establish a connection to the client. |
| { 	"client\_type": "blade\_runner", 	"message": "STAT", 	"client\_id": "BR01", 	"timestamp": 1000, 	"status": "ON/OFF/ERR/CRASH/MANSTP", 	"direction": "LEFT/RIGHT" } | Sends the trains current status back to the MCP, MANSTP is an optional status for HD trains which is sent if a train stops due to local collision detection. A station maybe on a stations left or right in which case the MCP tells the blade runner which side the station is on to open the correct set of doors. |
| { 	"type": "station", 	"action": "STAT", 	"client\_id": "ST01", 	"timestamp": 2000, 	"status": "ON/OFF/ERR", 	"door\_status": "OPEN/CLOSE" } | Sends the stations current status back to the MCP |

# Inputs/Outputs {#inputs/outputs}

## Blade Runner {#blade-runner}

| Output | Expected Data | Description |
| :---- | :---- | :---- |
| Door Status (String) | OPEN, CLOSE, ERR | Whether the door is open or closed |
| Carriage Status (String) | ON, ERR | Whether the carriage is operational and ok |
| Speed (Integer) | VALUE (Measured in cm/s) can be negative to signal reverse | What speed the blade runner is currently travelling at can be measured with a rotary encoder or an IMU etc.  |

## 

| Input | Expected Data | Description |
| :---- | :---- | :---- |
| Speed (Integer) | VALUE (Measured in cm/s) can be negative to signal reverse | An exact speed value the train must travel at |
| Carriage Status (String) | ON, ERR | What status the train should become or be given by the MCP |
| Door Status (String) | OPEN, CLOSE, ERR | The MCP will tell the train to open and close doors |

## Stations {#stations}

| Output | Expected Data | Description |
| :---- | :---- | :---- |
| Door Status (String) | OPEN, CLOSE, ERR | Whether the door is open or closed |
| Stations Status (String) | ON, ERR | Whether the station is operational and ok |

## 

| Input | Expected Data | Description |
| :---- | :---- | :---- |
| Door Status (String) | OPEN, CLOSE, ERR | The MCP will tell the train to open and close doors |

# Error Handling {#error-handling}

| MCP unresponsive or disconnected | Loss of Connection/MCP hangs Bladerunners stop Attempt re-connection Follow back startup process as mentioned earlier |
| :---- | :---- |
| CCP unresponsive or disconnected | All blade runners will be told to stop and go to the nearest station if possible.  |