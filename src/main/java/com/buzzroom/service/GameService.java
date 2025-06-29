package com.buzzroom.service;

import com.buzzroom.controller.SseController;
import com.buzzroom.model.Player;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {

    private final Map<Integer, Player> players = new HashMap<>();
    private Integer activePlayerId = null;
    private boolean buzzLocked = false;

    private final SseController sseController;

    public GameService(SseController sseController) {
        this.sseController = sseController;
    }

    public String handleBuzz(int id) {
        players.putIfAbsent(id, new Player(id));
        Player p = players.get(id);

        if (buzzLocked) return "Un joueur a déjà buzzé.";
        if (p.isBlocked()) return "Joueur bloqué.";

        buzzLocked = true;
        activePlayerId = id;

        sseController.sendEvent("buzz:" + id); // 🔔 Notifie l'interface web

        return "Joueur " + id + " a buzzé.";
    }

    public String validateAnswer(int id) {
        if (!Objects.equals(activePlayerId, id)) return "Ce joueur n'est pas actif.";

        players.get(id).addScore(1);
        sseController.sendEvent("valid:" + id); // 🎯 Notifie le front : bonne réponse

        reset();
        return "Bonne réponse. +1 point.";
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