package com.system.GUI;

import com.system.entities.CPU;
import com.system.entities.Registers;
import com.system.handlers.enumCommands;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class Interface extends JFrame {
    private JButton RunAll;
    private JButton Run;
    private JButton SaveState;
    private JButton LoadState;
    private JButton SetNormal;
    private JButton Reset;
    private JButton Add;
    private JButton POP;
    private JButton saveFile;
    private JButton loadFile;
    private JTextField PCField;
    private JTextField AccumulatorField;
    private JTextField StateField;
    private JTextField SavedPC;
    private JTextField Argument;
    private JTextField SavedA;
    private JTextField SavedState;
    private JTextField currentInstruction;

    private JPanel mainPanel;
    private JPanel buttonPanel;
    private JPanel regPanel;
    private JPanel instPanel;
    private JPanel filePanel;
    private JFormattedTextField MemoryField;
    private JTextArea InstructionsField;
    private JComboBox<String> Instruction;
    private JTextField InputField;
    private JTextField LoadFileField;
    private JTextField SaveFileField;

    private final CPU cpu;
    public Registers reg;

    private void updateMemory() {
        MemoryField.setText(Arrays.toString(cpu.getMemory()));
        InstructionsField.setText(cpu.getInstructionsToString());
        currentInstruction.setText("Instruction: " + cpu.getInstructionToString(cpu.getPC()));
    }
    private void updateText() {
        PCField.setText("PC: " + cpu.getPC());
        AccumulatorField.setText("Accumulator: " + cpu.getAccumulator());
        StateField.setText("State: " + cpu.getState().toString());
    }

    public Interface(CPU cpu) {
        super();

        this.cpu = cpu;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension dimension = new Dimension(680,230);
        mainPanel.setMinimumSize(dimension);
        mainPanel.setPreferredSize(dimension);
        mainPanel.setMaximumSize(dimension);

        this.setContentPane(mainPanel);
        this.setResizable(false);
        this.setVisible(true);
        this.pack();

        for(enumCommands v : enumCommands.values()) {
            Instruction.addItem(v.toString());
        }

        updateMemory();
        updateText();

        RunAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cpu.executeAll();
                updateMemory();
                updateText();
            }
        });
        Run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cpu.execute();
                updateMemory();
                updateText();
            }
        });
        SaveState.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                reg = cpu.getRegisters();
                SavedPC.setText("PC: " + cpu.getPC());
                SavedA.setText("Accumulator: " + cpu.getAccumulator());
                SavedState.setText("State: " + cpu.getState().toString());
            }
        });
        LoadState.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (reg != null) {
                    cpu.setRegister(reg);
                    updateText();
                }
            }
        });
        SetNormal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cpu.setCpuStopToNormal();
                updateText();
                currentInstruction.setText("Instruction: " + cpu.getInstructionToString(cpu.getPC()));
            }
        });

        Reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cpu.clearInstructions();
                updateMemory();
                updateText();
            }
        });
        Add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cpu.insertInstruction(Instruction.getSelectedItem() + " " + Argument.getText());
                updateMemory();
            }
        });
        POP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cpu.popInstruction();
                updateMemory();
                PCField.setText("PC: " + cpu.getPC());
            }
        });
        loadFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(null, cpu.loadFile(LoadFileField.getText()));
                updateText();
                updateMemory();
            }
        });
        saveFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(null, cpu.saveFile(SaveFileField.getText()));
            }
        });
    }
}
