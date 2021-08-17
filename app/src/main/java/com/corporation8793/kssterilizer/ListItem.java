package com.corporation8793.kssterilizer;

public class ListItem {

    private String machine_id;
    private Boolean machine_check;
    private Boolean empty;

    public String getMachine_id() {
        return machine_id;
    }

    public Boolean getMachine_check() {
        return machine_check;
    }

    public Boolean getEmpty() {
        return empty;
    }

    public void setEmpty(Boolean empty) {
        this.empty = empty;
    }

    public void setMachine_id(String machine_id) {
        this.machine_id = machine_id;
    }

    public void setMachine_check(Boolean machine_check) {
        this.machine_check = machine_check;
    }
}