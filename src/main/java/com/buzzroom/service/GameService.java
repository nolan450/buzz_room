package com.buzzroom.service;

import com.buzzroom.controller.SseController;
import com.buzzroom.model.Player;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class GameService {

    private final Map<Integer, Player> players = new HashMap<>();
    private Integer activePlayerId = null;
    private boolean buzzLocked = false;

    private final SseController sseController;
    private final Lock lock = new ReentrantLock();


    public GameService(SseController sseController) {
        this.sseController = sseController;
    }

    public void registerPlayers(int count) {
        players.clear();
        for (int i = 1; i <= count; i++) {
            players.put(i, new Player(i));
        }
        sseController.sendEvent("register:" + count);
    }

    public String handleBuzz(int id) {
        System.out.println("Buzz");
        lock.lock();
        System.out.println("Buzz after lock");
        try {
            players.putIfAbsent(id, new Player(id));
            Player p = players.get(id);

            if (buzzLocked) {
                System.out.println("Buzz locked");
                return "Un joueur a déjà buzzé.";
            }
            if (p.isBlocked()) {
                System.out.println("Joueur bloqué");
                return "Joueur bloqué.";
            }

            buzzLocked = true;
            activePlayerId = id;

            sseController.sendEvent("buzz:" + id); // 🔔 Notifie le front

            // 🕒 Débloque après 10 secondes
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    lock.lock();
                    try {
                        System.out.println("⏳ Déverrouillage après 10s");
                        buzzLocked = false;
                        activePlayerId = null;
                        sseController.sendEvent("reset");
                    } finally {
                        lock.unlock();
                    }
                }
            }, 10_000); // 10 sec

            return "Joueur " + id + " a buzzé.";
        } finally {
            System.out.println("Unlocking buzz");
            lock.unlock();
        }
    }


    public String validateAnswer(int id) {
        if (!Objects.equals(activePlayerId, id)) return "Ce joueur n'est pas actif.";

        players.get(id).addScore(1);
        sseController.sendEvent("valid:" + id);

        //reset();
        return "Bonne réponse. +1 point.";
    }

    public String penalize(int id) {
        if (!Objects.equals(activePlayerId, id)) return "Ce joueur n'est pas actif.";

        players.get(id).addScore(-1); // ou 0 si tu ne veux pas retirer de points
        sseController.sendEvent("penalize:" + id);

        reset();
        return "Mauvaise réponse. -1 point.";
    }

    public String reset() {
        buzzLocked = false;
        activePlayerId = null;

        // ⛔ On peut aussi notifier le front d’un reset
        sseController.sendEvent("reset");

        return "Tour réinitialisé.";
    }

    public List<Player> getScores() {
        return new ArrayList<>(players.values());
    }
}