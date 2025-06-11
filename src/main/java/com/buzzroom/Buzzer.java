package com.buzzroom;

public class Buzzer {
    private final int id;
    private int latencyMs;

    public Buzzer(int id, int latencyMs) {
        this.id = id;
        this.latencyMs = latencyMs;
    }

    public int getId() {
        return id;
    }

    public int getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(int latencyMs) {
        this.latencyMs = latencyMs;
    }

    public long buzz() {
        return System.currentTimeMillis() + latencyMs;
    }
}
