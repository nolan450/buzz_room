package com.buzzroom;
import java.util.concurrent.locks.Condition;

class Buzzer {
    private final int id;
    private Condition condition;

    public Buzzer(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
}