package com.system.entities;

public class PageDescriber {

    private final int id;

    private boolean is_valid = false;
    private boolean is_changeable = true;
    private boolean was_changed = false;
    private boolean was_accessed = false;

    private int frame = -1;

    public PageDescriber(int id) {
        this.id = id;
    }

    public boolean isValid() {
        return is_valid;
    }
    public void setValid(boolean i) {
        this.is_valid = i;
    }
    public boolean is_changeable() {
        return is_changeable;
    }
    public void setChangeable(boolean changeable) {
        this.is_changeable = changeable;
    }
    public boolean was_accessed() {
        return was_accessed;
    }
    public void setAccessed(boolean accessed) {
        this.was_accessed = accessed;
    }
    public boolean was_changed() {
        return was_changed;
    }
    public void setChanged(boolean changed) {
        this.was_changed = changed;
    }
    public int getFrame() {
        return frame;
    }
    public void setFrame(int frame) {
        this.frame = frame;
    }
    public int getId() {
        return id;
    }
}
