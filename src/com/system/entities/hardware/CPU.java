package com.system.entities.hardware;

import com.system.entities.os.Process;
import com.system.entities.memory.MMU;
import com.system.handlers.Tuple;
import com.system.handlers.enumCommands;
import com.system.handlers.enumState;
import com.system.handlers.enumStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static com.system.handlers.VarsMethods.mySplit;
import static com.system.handlers.VarsMethods.tryEnum;


public class CPU {

    private Registers registers;

    private int sizeProgram;

    private MMU mmu;


    /**
     *  First Integer: instruction index
     *  Second Integer: argument (if needed)
     */
    private ArrayList<Tuple<Integer, Integer>> memoryInstructions = new ArrayList<>();

    public CPU(MMU mmu){
        this.mmu = mmu;
    }
    // Setup empty CPU based on register
    public CPU (Registers reg, MMU mmu) {
        this.registers.setPC(reg.getPC());
        this.registers.setState(reg.getState());
        this.registers.setAccumulator(reg.getAccumulator());
    }

    public void loadJob(Process job) {
        //this.memory = job.getMemory();
        this.registers = job.getRegisters();
        this.registers.setState(job.getState());
        this.memoryInstructions = job.getInstructions();
        this.sizeProgram = memoryInstructions.size();
    }

    private void insertInstruction(String[] myString) {
        for(String str : myString)
            insertInstruction(str, this.memoryInstructions, registers);
        setSizeProgram();
    }
    public static void insertInstruction(String myString, ArrayList<Tuple<Integer, Integer>> array, Registers reg) {
        String[] parsed = mySplit(myString, " ");

        if (parsed.length > 2)
            reg.setState(enumState.InvalidInstructions);

        String cmd = parsed[0];
        enumCommands myEnum = tryEnum(cmd);

        if (parsed.length > 1) {
            array.add(new Tuple<>(myEnum.getCommand(), Integer.parseInt(parsed[1])));
            if(!CPU.hasArgument(myEnum.getCommand()))
                reg.setState(enumState.InvalidInstructions);
        }
        else {
            array.add(new Tuple<>(myEnum.getCommand(), null));
            if(CPU.hasArgument(myEnum.getCommand()))
                reg.setState(enumState.InvalidInstructions);
        }

    }

    public Tuple<Integer, Integer> getInstruction(int PC) {
        if (PC < getSizeProgram())
            return memoryInstructions.get(PC);
        else {
            this.registers.setPC(this.registers.getPC() - 1);
            return new Tuple<>(enumCommands.ERROR.getCommand(), null);
        }
    }

    // Instructions function -------------------------------------

    private void CARGI(int n) {
        registers.setAccumulator(n);
    }
    private void CARGM(int n) {
        switch (mmu.check(n)) {
            case 0:
                setPageFault();
                break;
            case 1:
                mmu.setRead(n);
                registers.setAccumulator(mmu.read(n));
                break;
            case -1:
                registers.setState(enumState.InvalidMemory);
                break;
            default:
                System.out.println("unknown code error");
        }
    }
    private void CARGX(int n) {
        switch (mmu.check(n)) {
            case 0:
                setPageFault();
                break;
            case 1:
                switch (mmu.check(mmu.read(n))) {
                    case 0:
                        setPageFault();
                        break;
                    case 1:
                        mmu.setRead(n);
                        mmu.setRead(mmu.read(n));
                        registers.setAccumulator(mmu.read(mmu.read(n)));
                        break;
                    case -1:
                        registers.setState(enumState.InvalidMemory);
                        break;
                    default:
                        System.out.println("unknown code error");
                }
                break;
            case -1:
                registers.setState(enumState.InvalidMemory);
                break;
            default:
                System.out.println("unknown code error");
        }
    }
    private void ARMM(int n) {
        switch (mmu.check(n)) {
            case 0:
                setPageFault();
                break;
            case 1:
                mmu.setWrote(n);
                mmu.write(registers.getAccumulator(), n);
                break;
            case -1:
                registers.setState(enumState.InvalidMemory);
                break;
            default:
                System.out.println("unknown code error");
        }
    }
    private void ARMX(int n) {
        switch (mmu.check(n)) {
            case 0:
                setPageFault();
                break;
            case 1:
                switch (mmu.check(mmu.read(n))) {
                    case 0:
                        setPageFault();
                        break;
                    case 1:
                        mmu.setRead(n);
                        mmu.setWrote(mmu.read(n));
                        mmu.write(registers.getAccumulator(), mmu.read(n));
                        break;
                    case -1:
                        registers.setState(enumState.InvalidMemory);
                        break;
                    default:
                        registers.setState(enumState.InvalidMemory);
                        System.out.println("unknown code error");
                }
                break;
            case -1:
                registers.setState(enumState.InvalidMemory);
                break;
            default:
                registers.setState(enumState.InvalidMemory);
                System.out.println("unknown code error");
        }
    }
    private void SOMA(int n) {
        switch (mmu.check(n)) {
            case 0:
                setPageFault();
                break;
            case 1:
                mmu.setRead(n);
                registers.setAccumulator(registers.getAccumulator() + mmu.read(n));
                break;
            case -1:
                registers.setState(enumState.InvalidMemory);
                break;
            default:
                System.out.println("unknown code error");
        }
    }
    private void NEG() {
        this.registers.setAccumulator(this.registers.getAccumulator()*-1);
    }
    private void DESVZ(int n) {
        if ( registers.getAccumulator() == 0 )
            registers.setPC(n);
    }
    private void PARA() {
        registers.setState(enumState.Stop);
    }
    private void LE(int n) {
        registers.setState(enumState.InvalidInstructions);
    }
    private void GRAVA(int n) {
        registers.setState(enumState.InvalidInstructions);
    }
    private void ERROR() {
        registers.setState(enumState.InvalidInstructions);
        this.registers.setPC(this.registers.getPC() - 1);
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
        if (registers.getPC() >= memoryInstructions.size()) {
            registers.setState(enumState.InvalidInstructions);
            return enumStatus.Error.getStatus();
        }

        Tuple<Integer, Integer> aux = memoryInstructions.get(registers.getPC());

        int inst = aux.getX();
        Object arg = aux.getY();

        System.out.println("Instruction " + registers.getPC() + ": " + enumCommands.values()[inst] + " " + (arg == null ? "" : arg));

        registers.setPC(registers.getPC() + 1);
        getInstruction[inst].execute(arg);

        if (registers.getState() == enumState.InvalidInstructions || registers.getState() == enumState.PageFault)
            return enumStatus.Syscall.getStatus();
        if (registers.getState() == enumState.InvalidMemory)
            return enumStatus.Error.getStatus();
        if(isCpuStop())
            return enumStatus.Stop.getStatus();

        return enumStatus.Next.getStatus();
    }

    public void clearInstructions() {
        memoryInstructions.clear();
        setSizeProgram();

        registers.setPC(0);
        registers.setAccumulator(0);
        registers.setState(enumState.Normal);
    }

    public int getSizeProgram() { return sizeProgram; }

    private void setSizeProgram() { sizeProgram = memoryInstructions.size(); }

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
        this.registers.setState(state);
    }

    public boolean isCpuStop() { return registers.getState() != enumState.Normal; }

    // Save register into a file
    public void creteLog(String file) {
        try {

            FileWriter myWriter = new FileWriter(file);

            myWriter.write("PC: " + this.registers.getPC() + "\n");
            myWriter.write("Accumulator: " + this.registers.getAccumulator() + "\n");
            //myWriter.write("Memory: " + Arrays.toString(this.memory) + "\n");

            myWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred in file create.");
            e.printStackTrace();
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

    private void setPageFault() {
        registers.setPC(this.getPC()-1);
        setCpuState(enumState.PageFault);
    }

    public int getAccumulator() {
        return registers.getAccumulator();
    }

    public void setAccumulator(int i) { this.registers.setAccumulator(i); }

    public int getPC() {
        return registers.getPC();
    }

    public enumState getState() { return registers.getState(); }

}
