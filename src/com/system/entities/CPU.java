package com.system.entities;

import com.system.handlers.Tuple;
import com.system.handlers.enumCommands;
import com.system.handlers.enumState;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;


public class CPU {

    private int[][] IO;
    private int[] counter;
    private int[] cost;

    private Registers registers;

    private int[] memory;
    private int sizeProgram;


    /**
     *  First Integer: instruction index
     *  Second Integer: argument (if needed)
     */
    private ArrayList<Tuple<Integer, Integer>> memoryInstructions = new ArrayList<>();


    // Setup empty CPU based on register
    public CPU (Registers reg) {
        this.registers.PC = reg.getPC();
        this.registers.State = reg.getState();
        this.registers.Accumulator = reg.getAccumulator();
    }

    // Setup CPU with memory allocated
    public CPU (int i, int j) {
        this.registers.PC = 0;
        this.registers.State = enumState.Normal;
        this.registers.Accumulator = 0;
        this.memory = new int[i];
        this.IO = new int[j][i];
        this.counter = new int[j];
        this.cost = new int[j];
    }

    // Starts a full empty CPU
    public CPU () {
        this.registers.PC = 0;
        this.registers.State = enumState.Normal;
        this.registers.Accumulator = 0;
    }

    public CPU (Process job) {
        this.memory = job.getMemory();
        this.registers = job.getRegisters();
        this.IO = job.getIO();
        this.counter = job.getCounter();
        this.cost = job.getCost();
        this.registers.State = job.getState();
        this.memoryInstructions = job.getInstructions();
        this.sizeProgram = memoryInstructions.size();
    }

    public void insertInstruction(String[] myString) {
        for(String str : myString)
            insertInstruction(str);
        setSizeProgram();
    }
    public void insertInstruction(String myString) {
        String[] parsed = mySplit(myString, " ");

        if (parsed.length > 2) {
            System.out.println(Arrays.toString(parsed));
            setCpuStop(enumState.InvalidInstructions);
        }
        String cmd = parsed[0];
        enumCommands myEnum = tryEnum(cmd);

        if (parsed.length > 1)
            memoryInstructions.add(new Tuple<>(myEnum.getCommand(), Integer.parseInt(parsed[1])));
        else
            memoryInstructions.add(new Tuple<>(myEnum.getCommand(), null));

        setSizeProgram();
    }

    protected Tuple<Integer, Integer> getInstruction(int PC) {
        if (PC < getSizeProgram())
            return memoryInstructions.get(PC);
        else {
            this.registers.PC--;
            return new Tuple<>(enumCommands.ERROR.getCommand(), null);
        }
    }

    // Instructions function -------------------------------------

    private void CARGI(int n) {
        registers.Accumulator = n;
    }
    private void CARGM(int n) {
        if(n < memory.length)
            registers.Accumulator = memory[n];
        else
            setCpuStop(enumState.InvalidMemory);
    }
    private void CARGX(int n) {
        if(n < memory.length) {
            int aux = memory[n];
            if (aux < memory.length) {
                registers.Accumulator = memory[memory[n]];
                return;
            }
        }
        setCpuStop(enumState.InvalidMemory);
    }
    private void ARMM(int n) {
        if(n < memory.length)
            memory[n] = registers.Accumulator;
        else
            setCpuStop(enumState.InvalidMemory);
    }
    private void ARMX(int n) {
        if(n < memory.length) {
            int aux = memory[n];
            if (aux < memory.length) {
                memory[memory[n]] = registers.Accumulator;
                return;
            }
        }
        setCpuStop(enumState.InvalidMemory);
    }
    private void SOMA(int n) {
        if(n < memory.length)
            registers.Accumulator += memory[n];
        else
            setCpuStop(enumState.InvalidMemory);
    }
    private void NEG() {
        registers.Accumulator *= -1;
    }
    private void DESVZ(int n) {
        if ( registers.Accumulator == 0 ) {
            registers.PC = n;
        }
    }
    private void PARA() {
        setCpuStop(enumState.Stop);
    }
    private void LE(int n) {
        registers.Accumulator = IO[n][counter[n]];
        counter[n]++;
        setCpuStop(enumState.Sleep);
    }
    private void GRAVA(int n) {
        IO[n][counter[n]] = registers.Accumulator;
        counter[n]++;
        setCpuStop(enumState.Sleep);
    }
    private void ERROR() {
        setCpuStop(enumState.InvalidInstructions);
        registers.PC--;
    }

    private interface instruction {
        void execute(Object i);
    }

    private final CPU.instruction[] getInstruction = new CPU.instruction[] {
            n -> CARGI((int) n),
            n -> CARGM((int) n),
            n -> CARGX((int) n),
            n -> ARMM((int) n),
            n -> ARMX((int) n),
            n -> SOMA((int) n),
            n -> NEG(),
            n -> DESVZ((int) n),
            n -> PARA(),
            n -> LE((int) n),
            n -> GRAVA((int) n),
            n -> ERROR(),
    };

    // Instructions function -------------------------------------

    // Sees if an instruction has mandatory argument
    public static boolean hasArgument(int i) {
        return i != enumCommands.NEG.getCommand() && i != enumCommands.PARA.getCommand() && i != enumCommands.ERROR.getCommand();
    }

    public int execute() {
        if(isCpuStop())
            return -1;
        if (registers.PC >= memoryInstructions.size()) {
            setCpuStop(enumState.InvalidInstructions);
            return -1;
        }

        Tuple<Integer, Integer> aux = memoryInstructions.get(registers.PC);

        int inst = aux.getX();
        Object arg = aux.getY();

        System.out.println("instrucao: " + registers.PC + " : " + inst + " " + arg);

        if((!hasArgument(inst) && arg != null ) || (hasArgument(inst) && arg == null)) {
            System.out.println("oi");
            setCpuStop(enumState.InvalidInstructions);
            return -1;
        }
        registers.PC++;
        getInstruction[inst].execute(arg);

        if((inst == enumCommands.GRAVA.getCommand() || inst == enumCommands.LE.getCommand()) && arg != null)
            return cost[(int) arg];

        return -1;
    }

    public void popInstruction() {
        if (getSizeProgram() <= 0)
            return;
        memoryInstructions.remove(memoryInstructions.get(memoryInstructions.size()-1));
        setSizeProgram();
        if(registers.PC >= getSizeProgram() && registers.PC > 0)
            registers.PC--;
    }

    public void clearInstructions() {
        memoryInstructions.clear();
        setSizeProgram();

        registers.PC = 0;
        registers.Accumulator=0;
        setCpuStop(enumState.Normal);
    }

    protected int getSizeProgram() { return sizeProgram; }

    protected void setSizeProgram() { sizeProgram = memoryInstructions.size(); }

    // Returns all instructions into String format
    public String getInstructionsToString() {
        StringBuilder ret = new StringBuilder();
        int i = 0;
        for(Tuple<Integer, Integer> v : memoryInstructions){
            ret.append(i).append(": ").append(enumCommands.values()[v.getX()]).append(" ");
            if (v.getY() == null)
                ret.append('\n');
            else
                ret.append(v.getY()).append('\n');
            i++;
        }
        return ret.toString();
    }

    public void setCpuStop(enumState i) {
        registers.State = i;
    }

    public boolean isCpuStop() { return registers.State != enumState.Normal; }

    // Save register into a file
    public void creteLog(String file) {
        try {

            FileWriter myWriter = new FileWriter(file);

            myWriter.write("PC: " + this.registers.PC + "\n");
            myWriter.write("Accumulator: " + this.registers.Accumulator + "\n");
            myWriter.write("Memory: " + Arrays.toString(this.memory) + "\n");

            myWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred in file create.");
            e.printStackTrace();
        }
    }

    // Set CPU state back to normal, in case was a normal interruption, increment PC
    public void setCpuStopToNormal() {
        if ( registers.State != enumState.Normal ) {
            this.registers.State = enumState.Normal;
        }
    }

    // Return String of an i instruction
    public String getInstructionToString(int i) {
        Tuple<Integer, Integer> tpl = getInstruction(i);

        if(tpl.getY() != null)
            return "" + enumCommands.values()[tpl.getX()] + " " + tpl.getY();
        return "" + enumCommands.values()[tpl.getX()];
    }

    // Clear instructions Array and creates a new one
    public void resetInstructions(String[] myString) {
        clearInstructions();
        this.insertInstruction(myString);
    }

    // Save instructions into a file
    public String saveFile(String str) {
        try {
            FileWriter myWriter = new FileWriter(str);
            for (int i = 0; i < getSizeProgram(); i++) {
                myWriter.write(getInstructionToString(i) + "\n");
            }
            myWriter.close();

            return "File " + str + " created.";

        } catch (IOException e) {
            e.printStackTrace();
            return "An error occurred.";
        }
    }
    // Load instructions from a file
    public String loadFile(String str) {
        try {
            File myObj = new File(str);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                this.insertInstruction(data);
            }
            myReader.close();

            return "File " + str + " loaded.";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "An error occurred.";
        }
    }

    public void setUpMemory (int i) {
        this.memory = new int[i];
    }

    public void changeMemory(int[] n){
        for (int i = 0; i < n.length && i < this.memory.length; i++)
            this.memory[i] = n[i];
    }

    public void setMemory(int[] i) {
        this.memory = i;
    }

    public void resetMemory() {
        Arrays.fill(this.memory, 0);
    }

    public int[] getMemory() {
        return memory;
    }

    public int getAccumulator() {
        return registers.Accumulator;
    }

    public void setAccumulator(int i) { this.registers.Accumulator = i; }

    public int getPC() {
        return registers.PC;
    }

    public void setRegister(Registers reg) {
        this.registers = reg;
    }

    public Registers getRegisters() {
        return registers;
    }

    public enumState getState() { return registers.State; }

    public static String[] mySplit(String str, String regex) {
        Vector<String> result = new Vector<>();
        int start = 0;
        int pos = str.indexOf(regex);
        while (pos>=start) {
            if (pos>start) {
                result.add(str.substring(start,pos));
            }
            start = pos + regex.length();
            pos = str.indexOf(regex,start);
        }
        if (start<str.length()) {
            result.add(str.substring(start));
        }
        return result.toArray(new String[0]);
    }

    public static enumCommands tryEnum (String myString) {
        try {
            return Enum.valueOf(enumCommands.class, myString);
        } catch (IllegalArgumentException e) {
            // log error or something here
            return Enum.valueOf(enumCommands.class, "ERROR");
        }
    }
}
