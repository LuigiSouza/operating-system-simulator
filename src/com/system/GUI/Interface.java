package com.system.GUI;

import com.system.entities.CPU;
import com.system.entities.Registers;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class Interface extends JFrame {
    private JTextField MemoryField;
    private JTextArea InstructionsField;
    private JButton RunAll;
    private JButton Run;
    private JTextField PCField;
    private JTextField AccumulatorField;
    private JTextField StateField;
    private JButton SaveState;
    private JTextField SavedPC;
    private JButton LoadState;
    private JButton SetNormal;
    private JComboBox Instruction;
    private JTextField Argument;
    private JButton Reset;
    private JButton Add;
    private JButton POP;
    private JTextField SavedA;
    private JTextField SavedState;
    private JButton saveFile;
    private JButton loadFile;

    public CPU cpu = new CPU(8);
    public Registers reg;

    private JPanel mainPanel;
    private JPanel buttonPanel;
    private JPanel regPanel;
    private JPanel instPanel;
    private JPanel filePanel;
    private JTextField currentInstruction;

    public Interface() {

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        //this.setResizable(false);
        this.setVisible(true);
        this.pack();

        cpu.loadFile("filename.txt");

        MemoryField.setText(Arrays.toString(cpu.getMemory()));
        InstructionsField.setText(cpu.getInstructions());
        currentInstruction.setText("Current Instruction: " + cpu.getInstructionStr());

        RunAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cpu.executeAll();
                MemoryField.setText(Arrays.toString(cpu.getMemory()));
                currentInstruction.setText("Current Instruction: " + cpu.getInstructionStr());
            }
        });
        Run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cpu.execute();
                MemoryField.setText(Arrays.toString(cpu.getMemory()));
                currentInstruction.setText("Current Instruction: " + cpu.getInstructionStr());
            }
        });
        SaveState.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                reg = cpu.getRegisters();
            }
        });
        LoadState.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (reg != null)
                    cpu.setRegister(reg);
            }
        });
        SetNormal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cpu.setCpuStopToNormal();
            }
        });
    }
}
