package com.buzzroom.model;

public class Player {
    private int id;
    private int score = 0;
    private boolean blocked = false;

    public Player(int id) {
        this.id = id;
    }

    public int getId() { return id; }
    public int getScore() { return score; }
    public boolean isBlocked() { return blocked; }

    public void addScore(int delta) {
        this.score += delta;
    }

    public void block() {
        this.blocked = true;
    }

    public void unblock() {
        this.blocked = false;
    }
}
