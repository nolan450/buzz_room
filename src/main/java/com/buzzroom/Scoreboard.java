package com.buzzroom;

import java.util.HashMap;
import java.util.Map;

public class Scoreboard {
    private final Map<Integer, Integer> scores = new HashMap<>();

    public void addPlayer(int id) {
        scores.putIfAbsent(id, 0);
    }

    public void updateScore(int id, int delta) {
        scores.put(id, scores.getOrDefault(id, 0) + delta);
    }

    public int getScore(int id) {
        return scores.getOrDefault(id, 0);
    }

    public void printScores() {
        scores.forEach((id, score) ->
                System.out.println("Joueur " + id + " : " + score + " points"));
    }

    public void clear() {
        scores.clear();
    }
}