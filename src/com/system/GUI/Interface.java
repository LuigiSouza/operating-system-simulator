package com.system.GUI;

import com.system.entities.CPU;
import com.system.entities.Registers;
import com.system.entities.SO;
import com.system.handlers.enumCommands;
import com.system.handlers.enumState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Objects;

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
    private JFormattedTextField MemoryField;
    private JTextArea InstructionsField;
    private JComboBox<String> Instruction;
    private JTextField InputFieldFile;
    private JTextField LoadFileField;
    private JTextField SaveFileField;
    private JButton ResetMemory;
    private JButton loadIOFile;
    private JButton loadIO;
    private JTextField InputValue;

    private final SO system;
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

        this.system = new SO(8);
        this.cpu = system.getCpu();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension dimension = new Dimension(800,240);
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
                if(cpu.getState() == enumState.Stop) {
                    JOptionPane.showMessageDialog(null, "Program Terminated!");
                }
                updateMemory();
                updateText();
            }
        });
        Run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cpu.execute();
                if(cpu.getState() == enumState.Stop) {
                    JOptionPane.showMessageDialog(null, "Program Terminated!");
                }
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
        Instruction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                enumCommands aux = Enum.valueOf(enumCommands.class, Objects.requireNonNull(Instruction.getSelectedItem()).toString());
                if(cpu.hasArgument(aux.getCommand())) {
                    Argument.setEditable(true);
                }
                else {
                    Argument.setEditable(false);
                    Argument.setText("");
                }
            }
        });
        ResetMemory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cpu.resetMemory();
                MemoryField.setText(Arrays.toString(cpu.getMemory()));
            }
        });
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                e.getWindow().dispose();
            }
        });
    }
}
