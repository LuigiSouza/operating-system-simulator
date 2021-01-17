package com.system.entities;

import com.system.handlers.Tuple;
import com.system.handlers.enumCommands;
import com.system.handlers.enumState;
import com.system.handlers.enumStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

import static com.system.handlers.VarsMethods.mySplit;
import static com.system.handlers.VarsMethods.tryEnum;


public class CPU {

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

    public CPU (Process job) {
        this.memory = job.getMemory();
        this.registers = job.getRegisters();
        this.registers.State = job.getState();
        this.memoryInstructions = job.getInstructions();
        this.sizeProgram = memoryInstructions.size();
    }

    public void insertInstruction(String[] myString) {
        for(String str : myString)
            insertInstruction(str, this.memoryInstructions, registers);
        setSizeProgram();
    }
    public static void insertInstruction(String myString, ArrayList<Tuple<Integer, Integer>> array, Registers reg) {
        String[] parsed = mySplit(myString, " ");

        if (parsed.length > 2)
            reg.State = enumState.InvalidInstructions;

        String cmd = parsed[0];
        enumCommands myEnum = tryEnum(cmd);

        if (parsed.length > 1) {
            array.add(new Tuple<>(myEnum.getCommand(), Integer.parseInt(parsed[1])));
            if(!CPU.hasArgument(myEnum.getCommand()))
                reg.State = enumState.InvalidInstructions;
        }
        else {
            array.add(new Tuple<>(myEnum.getCommand(), null));
            if(CPU.hasArgument(myEnum.getCommand()))
                reg.State = enumState.InvalidInstructions;
        }

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
            registers.State = enumState.InvalidMemory;
    }
    private void CARGX(int n) {
        if(n < memory.length) {
            int aux = memory[n];
            if (aux < memory.length) {
                registers.Accumulator = memory[memory[n]];
                return;
            }
        }
        registers.State = enumState.InvalidMemory;
    }
    private void ARMM(int n) {
        if(n < memory.length)
            memory[n] = registers.Accumulator;
        else
            registers.State = enumState.InvalidMemory;
    }
    private void ARMX(int n) {
        if(n < memory.length) {
            int aux = memory[n];
            if (aux < memory.length) {
                memory[memory[n]] = registers.Accumulator;
                return;
            }
        }
        registers.State = enumState.InvalidMemory;
    }
    private void SOMA(int n) {
        if(n < memory.length)
            registers.Accumulator += memory[n];
        else
            registers.State = enumState.InvalidMemory;
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
        registers.State = enumState.Stop;
    }
    private void LE(int n) {
        registers.State = enumState.InvalidInstructions;
    }
    private void GRAVA(int n) {
        registers.State = enumState.InvalidInstructions;
    }
    private void ERROR() {
        registers.State = enumState.InvalidInstructions;
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
            return enumStatus.Stop.getStatus();
        if (registers.PC >= memoryInstructions.size()) {
            registers.State = enumState.InvalidInstructions;
            return enumStatus.Error.getStatus();
        }

        Tuple<Integer, Integer> aux = memoryInstructions.get(registers.PC);

        int inst = aux.getX();
        Object arg = aux.getY();

        System.out.println("Instruction " + registers.PC + ": " + enumCommands.values()[inst] + " " + (arg == null ? "" : arg));

        registers.PC++;
        getInstruction[inst].execute(arg);

        if (registers.State == enumState.InvalidInstructions)
            return enumStatus.Syscall.getStatus();
        if (registers.State == enumState.InvalidMemory)
            return enumStatus.Error.getStatus();
        if(isCpuStop())
            return enumStatus.Stop.getStatus();

        return enumStatus.Next.getStatus();
    }

    public void clearInstructions() {
        memoryInstructions.clear();
        setSizeProgram();

        registers.PC = 0;
        registers.Accumulator=0;
        registers.State = enumState.Normal;
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

    public void setCpuState(enumState state) {
        this.registers.State = state;
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
                insertInstruction(data, memoryInstructions, registers);
            }
            myReader.close();

            return "File " + str + " loaded.";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "An error occurred.";
        }
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

}
