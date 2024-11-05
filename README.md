# <ins>Team 3 Comms 3 ENGG2k/3k Carriage Control Program Repo</ins>

## CCP Members: Peter Martin, Joshua Watkins, Connell Ng, Andrew Parker, Hirune, Allen

## Description

This project consists of two programs. The CCP folder contains a program that acts as a server for communicating with another server called the MCP. The CCP receives commands from the MCP, processes these UDP packets, and then attempts to send them to the Blade Runner, a miniature carriage powered by an ESP32. This Arduino-based system establishes a connection with the CCP to receive commands in the sequence MCP -> CCP -> ESP32, and responds back in the sequence ESP32 -> CCP -> MCP. The Arduino determines its actions based on the information in the UDP packet. If the packet contains an "action:" request, it performs the specified action and responds with its updated status.
