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

    public CPU (Registers reg) {
        this.PC = reg.getPC();
        this.cpuStop = reg.getState();
        this.Accumulator = reg.getAccumulator();
    }

    public CPU (int i) {
        this.PC = 0;
        this.cpuStop = enumState.Normal;
        this.Accumulator = 0;
        this.memory = new int[i];
    }

    public CPU () {
        this.PC = 0;
        this.cpuStop = enumState.Normal;
        this.Accumulator = 0;
    }

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

    public void setCpuStopToNormal() {
        if ( cpuStop.getState() != enumState.Normal.getState() ) {
            this.cpuStop.setState(enumState.Normal.getState());
            PC++;
        }
    }

    @Override
    public Tuple<Integer, Integer> getInstruction(int PC) {
        if (PC < getSizeProgram())
            return super.getInstruction(PC);
        else
            return new Tuple<>(enumCommands.ERROR.getCommand(), null);
    }

    public String getInstructionStr() {
        Tuple<Integer, Integer> tpl = getInstruction(this.PC);

        if(tpl.getY() != null)
            return "" + enumCommands.values()[tpl.getX()] + " " + tpl.getY();
        return "" + enumCommands.values()[tpl.getX()];
    }

    public void resetInstructions(String[] myString) {
        super.clearInstructions();
        this.insertInstruction(myString);
    }

    public void loadFile(String str) {
        try {
            File myObj = new File(str);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                this.insertInstruction(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
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

    public int[] getMemory() {
        return memory;
    }

    public int getAccumulator() {
        return Accumulator;
    }

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

    public void setCpuStop(int i) {
        cpuStop.setState(i);
    }

    public boolean isCpuStop() {
        return cpuStop.getState() != 0;
    }

    public void executeAll() {
        while( !this.isCpuStop() ) {
            this.execute();
        }
    }
}