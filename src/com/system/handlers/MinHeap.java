package com.system.handlers;

import javax.swing.plaf.synth.SynthSpinnerUI;

// Java program to implement Max Heap
// Code from: https://www.geeksforgeeks.org/min-heap-in-java/
public class MinHeap {
    private Tuple<Integer,Integer>[] Heap;
    private int size;
    private int maxsize;

    // Constructor to initialize an
    // empty max heap with given maximum
    // capacity.
    public MinHeap(int maxsize)
    {
        this.maxsize = maxsize;
        this.size = 0;
        Heap = new Tuple[this.maxsize + 1];
        Heap[0] = new Tuple<>(Integer.MIN_VALUE, -1);
    }

    // Returns position of parent
    private int parent(int pos) { return pos / 2; }

    // Below two functions return left and
    // right children.
    private int leftChild(int pos) { return (2 * pos); }
    private int rightChild(int pos)
    {
        return (2 * pos) + 1;
    }

    // Returns true of given node is leaf
    private boolean isLeaf(int pos)
    {
        return pos > (size / 2) && pos <= size;
    }

    private void swap(int fpos, int spos)
    {
        Tuple tmp;
        tmp = Heap[fpos];
        Heap[fpos] = Heap[spos];
        Heap[spos] = tmp;
    }

    // A recursive function to max heapify the given
    // subtree. This function assumes that the left and
    // right subtrees are already heapified, we only need
    // to fix the root.
    private void minHeapify(int pos)
    {
        if (isLeaf(pos) || size <= 0)
            return;

        if (Heap[pos].getX() > Heap[leftChild(pos)].getX()
                || Heap[pos].getX() > Heap[rightChild(pos)].getX()) {

            if (Heap[leftChild(pos)].getX()
                    < Heap[rightChild(pos)].getX()) {
                swap(pos, leftChild(pos));
                minHeapify(leftChild(pos));
            }
            else {
                swap(pos, rightChild(pos));
                minHeapify(rightChild(pos));
            }
        }
    }

    // Inserts a new element to max heap
    public void insert(int time, int job)
    {
        if (size >= maxsize) {
            System.out.println("Maximum pauses achieved... No more is supported");
            return;
        }

        Heap[++size] = new Tuple<>(time, job);

        // Traverse up and fix violated property
        int current = size;
        while (Heap[current].getX() < Heap[parent(current)].getX()) {
            swap(current, parent(current));
            current = parent(current);
        }
    }

    public void print()
    {
        for (int i = 1; i <= size / 2; i++) {
            System.out.print(
                    " PARENT : " + Heap[i].getX() + " " + Heap[i].getY()
                            + " LEFT CHILD : " + (Heap[2 * i] != null ? (Heap[2 * i].getX() + " " + Heap[2 * i].getY()) : null)
                            + " RIGHT CHILD :" + (Heap[2 * i + 1] != null ? (Heap[2 * i + 1].getX() + " " + Heap[2 * i + 1].getY()) : null));
            System.out.println();
        }
    }

    public void remove_periodic() {
        int j = -1;
        for (int i = 1; i < size && j != i-1; i++) {
            if (Heap[i].getY() == -2)
                j = i;
        }

        if (j == -1) return;

        Tuple popped = Heap[j];
        Heap[j] = Heap[size--];

        minHeapify(1);
    }

    public Tuple<Integer, Integer> CheckMin() {
        return Heap[1];
    }
    // Remove an element from max heap
    public Tuple<Integer,Integer> extractMin()
    {
        Tuple popped = Heap[1];
        Heap[1] = Heap[size--];
        minHeapify(1);
        return popped;
    }

}
