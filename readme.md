# PlanetLang

Welcome to planet lang, a Stack based multiplayer programming language pushing collaborative coding to the next level.


## Overview
PlanetLang is a unique programming language that combines the principles of stack-based programming with real-time multiplayer collaboration. It allows multiple users to work together on the same codebase simultaneously, making it ideal for pair programming, coding interviews, and collaborative projects.

the idea is that the "server" is a planet, and each user is an astronaut on that planet. Each astronaut has their own stack, and they can push and pop values from their stack, as well as share values with other astronauts on the same planet.
the "server" has a global stack and memory that all astronauts can access.

since the server has a global stack and memory, the astronauts can use a instruction stack that allows them to save instructions to be executed later, either by themselves or by other astronauts.

the instruction stack can be used to implement control flow, functions, and other advanced programming concepts aswell as saving to the open global file.


## Features
- **Stack-Based Programming**: PlanetLang uses a stack-based approach, where operations are performed on a stack of values. Each user has their own private stack.
- **Multiplayer Collaboration**: Multiple users can connect to the same "planet" (server) and work together in real-time. Changes made by one user are instantly visible to all other users.
- **Shared Memory**: In addition to individual stacks, PlanetLang provides a shared memory space and global stack that all users can access and modify.
- **Real-time Synchronization**: Client GUIs automatically update every second to show the current state of global stacks and memory.
- **Simple Syntax**: PlanetLang has a simple and intuitive syntax that is easy to learn and use, even for beginners.
- **Extensible**: PlanetLang is designed to be extensible, allowing users to define their own functions and operations.
- **Interactive Environment**: PlanetLang provides an interactive environment where users can test and debug their code in real-time.

## Supported Instructions

### Stack Operations
- `push <number>` - Push a number onto the stack
- `pop` - Remove top value from stack
- `dup` - Duplicate top value on stack
- `swap` - Swap top two values on stack
- `clear` - Clear the entire stack

### Arithmetic Operations
- `add` - Add top two values (result on stack)
- `sub` - Subtract top two values (b - a)
- `mul` - Multiply top two values
- `div` - Divide top two values (b / a)

### Memory Operations
- `load <address>` - Load value from memory address
- `store <address>` - Store top stack value to memory address

### Utility Operations
- `print` - Print top stack value to console

### Multiplayer Operations
- **Share to Global**: Move top value from your stack to the global stack
- **Take from Global**: Move top value from global stack to your stack

## PlanetLang Instruction Set

### Stack Operations
- `push <value>` - Push a value onto the stack
- `pop` - Remove top value from stack
- `dup` - Duplicate top value on stack
- `swap` - Swap top two values on stack

### Arithmetic Operations
- `add` - Add top two values (result = a + b)
- `sub` - Subtract top two values (result = b - a)
- `mul` - Multiply top two values (result = a * b)
- `div` - Divide top two values (result = b / a)

### Memory Operations
- `load <address>` - Load value from memory address onto stack
- `store <address>` - Store top stack value to memory address

### Utility Operations
- `print` - Print top stack value
- `clear` - Clear entire stack

### Multiplayer Operations
- `SHARE` - Share top value from your stack to global stack
- `TAKE` - Take top value from global stack to your stack

## Execution Modes

### Local Execution
Commands prefixed with `LOCAL:` execute on your personal stack and memory space.

### Global Execution
Commands prefixed with `GLOBAL:` execute on the shared global stack and memory space visible to all connected users.

## Getting Started
To get started with PlanetLang, follow these steps:
1. Clone the repository: `git clone https://git.finite.ovh/PlanetLang.git`
2. Navigate to the project directory: `cd PlanetLang`
3. Build with maven: `mvn package`

## Running the Application
The PlanetLang system consists of a server and client components:

### Starting the Server
To start the PlanetLang server:
```bash
mvn exec:java -pl Server -Dexec.mainClass="org.finite.planetlangserver.PlanetlangServer"
```
The server will start on port 8000 by default.

### Starting the Client
To start the PlanetLang client GUI:
```bash
mvn exec:java -pl Client -Dexec.mainClass="org.finite.planetlangclient.PlanetLangClient"
```
The client will open a GUI window. Use the "Connection" menu to connect to the server by specifying the host (default: localhost) and port (default: 8000).

### Using the Client GUI
Once connected, you can:

1. **Execute Commands**: Type commands in the input field and select "Local" or "Global" execution mode
2. **View Stacks**: See your personal stack and the global shared stack in real-time
3. **Share Values**: Use "Share to Global" to move values from your stack to the global stack
4. **Take Values**: Use "Take from Global" to move values from the global stack to your stack
5. **View Memory**: Monitor the global shared memory space

### Connecting Client to Server
1. Start the server first
2. Start the client
3. In the client GUI, go to Connection > Connect to Server
4. Enter the server host (localhost for local testing)
5. Enter the server port (8000)
6. Click OK to connect

The client will show a success message when connected and display real-time updates of the global state.

## Examples

### Basic Stack Operations
```
push 5      # Stack: [5]
push 3      # Stack: [5, 3]
add         # Stack: [8]
print       # Prints: 8
```

### Memory Operations
```
push 42     # Stack: [42]
store 0     # Store 42 at memory address 0
clear       # Stack: []
load 0      # Stack: [42]
```

### Multiplayer Collaboration
User 1:
```
push 10     # Local stack: [10]
SHARE       # Global stack: [10], Local stack: []
```

User 2:
```
TAKE        # Local stack: [10], Global stack: []
push 5      # Local stack: [10, 5]
SHARE       # Global stack: [5], Local stack: [10]
```

### Calculator Example
```
push 15     # Stack: [15]
push 7      # Stack: [15, 7]
sub         # Stack: [8] (15 - 7 = 8)
push 3      # Stack: [8, 3]
mul         # Stack: [24] (8 * 3 = 24)
print       # Prints: 24
```
