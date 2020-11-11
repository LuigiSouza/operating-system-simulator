# Operating System Simulator
Simulator of a operating system to chair "ELC1080 - Sistemas Operacionais"

## Get started
All the API needs are located in CPU.java class, where are the main methods needed to run the application. For more specific changes, acces father class cpuBasic.java.

## Memory management
- `public void setUpMemory (int i)`

Initialize CPU memory with size i.
- `public void changeMemory(int[] n)`

Copy n[] to CPU memory.
- `public void setMemory(int[] i)`

Set CPU memory to reference i[].

## Instruction management
- `public void resetInstructions(String[] myString)`

Clean instructions Array and insert myString[] to the new one.
- `public Tuple<Integer, Integer> getInstruction(int PC)`

Returns a tuple with Instruction in X and argument in Y based on index(PC).
- `public void insertInstruction(String myString)`

Add myString instruction into instructions Array. Can receive a String[].
- `public void loadFile(String str)`

Load instructions from str file.

## Register management
- `public void setRegister(Registers reg)`

Set up PC, Accumulator and Stop Flag based on reg argument.
- `public Registers getRegisters()`

Return Register class with PC, Accumulator and Stop Flag

## Execute the program and manage Stop Flag
- `public void execute()`

Execute current instructions based on PC and increment it
- `public void executeAll()`

Execute instruction and increment PC on every cycle
- `public void setCpuStop(int i)`

Set CPU state based on enumState(i)
- `public boolean isCpuStop()`

Returns True if State isn't normal

