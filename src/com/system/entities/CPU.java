package com.system.entities;

import com.system.handlers.Tuple;
import com.system.handlers.enumCommands;
import com.system.handlers.enumState;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * cpu_altera_programa = insertInstruction
 * cpu_altera_dados = changeMemory
 * cpu_salva_dados = getMemory
 * cpu_interrupcao = setCpuStop
 * cpu_retorna_interrupcao = isCpuStop
 * cpu_instrucao = getInstruction
 * cpu_salva_estado = getRegister
 * cpu_altera_estado = setRegister
 * cpu_estado_inicializa = CPU constructor
 * cpu_executa = execute / executeAll
 */
public class CPU extends cpuBasic {

    // Setup empty CPU based on register
    public CPU (Registers reg) {
        this.PC = reg.getPC();
        this.cpuStop = reg.getState();
        this.Accumulator = reg.getAccumulator();
    }

    // Setup CPU with memory allocated
    public CPU (int i) {
        this.PC = 0;
        this.cpuStop = enumState.Normal;
        this.Accumulator = 0;
        this.memory = new int[i];
    }

    // Starts a full empty CPU
    public CPU () {
        this.PC = 0;
        this.cpuStop = enumState.Normal;
        this.Accumulator = 0;
    }

    // Save register into a file
    public void creteLog(String file) {
        try {

            FileWriter myWriter = new FileWriter(file);

            myWriter.write("PC: " + this.PC + "\n");
            myWriter.write("Accumulator: " + this.Accumulator + "\n");
            myWriter.write("Memory: " + Arrays.toString(this.memory) + "\n");

            myWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred in file create.");
            e.printStackTrace();
        }
    }

    // Set CPU state back to normal, in case was a normal interruption, increment PC
    public void setCpuStopToNormal() {
        if ( cpuStop != enumState.Normal ) {
            if(this.cpuStop == enumState.Read || this.cpuStop == enumState.Save)
                PC++;
            this.cpuStop = enumState.Normal;
        }
    }

    @Override
    public Tuple<Integer, Integer> getInstruction(int PC) {
        if (PC < getSizeProgram())
            return super.getInstruction(PC);
        else
            return new Tuple<>(enumCommands.ERROR.getCommand(), null);
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
        super.clearInstructions();
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
        return Accumulator;
    }

    public void setAccumulator(int i) { this.Accumulator = i; }

    public int getPC() {
        return PC;
    }

    public void setRegister(Registers reg) {
        this.PC = reg.getPC();
        this.Accumulator = reg.getAccumulator();
        this.cpuStop = reg.getState();
    }

    public Registers getRegisters() {
        return new Registers(getPC(), getAccumulator(), cpuStop);
    }

    public enumState getState() { return cpuStop; }

    public enumState executeAll() {
        while( !this.isCpuStop() && SO.timer.isInterruption(SO.timer.getTimer()) == null) {
            this.execute();
            SO.timer.addTimer();
        }
        return getState();
    }
}
