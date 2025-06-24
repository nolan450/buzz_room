package com.buzzroom;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class CLI {

    private final Map<Integer, Buzzer> buzzers = new HashMap<>();
    private final Scoreboard scoreboard = new Scoreboard();
    private int reactivityMs = 10;
    
    private Timer responseTimer = new Timer();
    private final Lock speechLock = new ReentrantLock();
    private volatile boolean isSpeaking = false;

    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        System.out.println("Bienvenue dans Buzz Room CLI !");
        System.out.println("Tape une commande (help pour la liste)");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            String[] args = input.split("\\s+");

            if (args.length == 0) continue;

            switch (args[0].toLowerCase()) {
                case "help":
                    printHelp();
                    break;
                case "init":
                    if (args.length < 2) {
                        System.out.println("Usage: init <nombre_buzzers>");
                    } else {
                        initBuzzers(Integer.parseInt(args[1]));
                    }
                    break;
                case "buzz":
                    if (args.length < 2) {
                        System.out.println("Usage: buzz <id_buzzer>");
                    } else {
                        triggerBuzz(Integer.parseInt(args[1]));
                    }
                    break;
                case "buzz-all":
                    simulateAllBuzzes();
                    break;
                case "list":
                    listBuzzers();
                    break;
                case "score":
                    if (args.length < 3) {
                        System.out.println("Usage: score <id> <+/-points>");
                    } else {
                        try {
                            int id = Integer.parseInt(args[1]);
                            int delta = Integer.parseInt(args[2]);
                            scoreboard.updateScore(id, delta);
                            System.out.println("Score du joueur " + id + " mis à jour.");
                        } catch (NumberFormatException e) {
                            System.out.println("Erreur : les arguments doivent être numériques.");
                        }
                    }
                    break;
                case "scores":
                    scoreboard.printScores();
                    break;
                case "set-reactivity":
                    if (args.length < 2) {
                        System.out.println("Usage: set-reactivity <ms>");
                    } else {
                        reactivityMs = Integer.parseInt(args[1]);
                        System.out.println("Réactivité définie à " + reactivityMs + " ms");
                    }
                    break;
                case "exit":
                    System.out.println("Fermeture de Buzz Room CLI...");
                    scanner.close();
                    return;
                case "reset":
                    resetGame();
                    break;
                default:
                    System.out.println("Commande inconnue. Tape 'help' pour l'aide.");
            }
        }
    }

    private void printHelp() {
        System.out.println("Commandes disponibles :");
        System.out.println("- init <nombre_buzzers> : initialise n buzzers");
        System.out.println("- buzz <id> : simule un buzz du buzzer");
        System.out.println("- buzz-all : simule tous les buzzers avec délai");
        System.out.println("- list : liste les buzzers actifs");
        System.out.println("- set-reactivity <ms> : délai entre les buzz simultanés");
        System.out.println("- score <id> <+/-points> : ajoute ou retire des points");
        System.out.println("- scores : affiche les scores de tous les joueurs");
        System.out.println("- reset : réinitialise le tour (débloque les buzzers)");
        System.out.println("- exit : quitter");
    }

    private void initBuzzers(int count) {
        buzzers.clear();
        scoreboard.clear();
        for (int i = 1; i <= count; i++) {
            final int buzzerId = i;
            Buzzer buzzer = new Buzzer(buzzerId);
            buzzers.put(buzzerId, buzzer);
            scoreboard.addPlayer(buzzerId);

            final Condition condition = speechLock.newCondition();
            buzzer.setCondition(condition);

            new Thread(() -> {
                while (true) {
                    speechLock.lock();
                    try {
                        condition.await();
                        if (!isSpeaking) {
                            isSpeaking = true;
                            System.out.println("Buzzer " + buzzerId + " a la parole.");
                            System.out.println("⏳ Le joueur " + buzzerId + " a 10 secondes pour parler...");

                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    speechLock.lock();
                                    try {
                                        System.out.println("🔔 Temps écoulé pour le joueur " + buzzerId + " !");
                                        isSpeaking = false;
                                    } finally {
                                        speechLock.unlock();
                                    }
                                }
                            }, 10_000);

                            while (isSpeaking) {
                                try {
                                    speechLock.unlock();
                                    Thread.sleep(100);
                                    speechLock.lock();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            timer.cancel();
                        } else {
                            System.out.println("Buzzer " + buzzerId + " a essayé de buzzer, mais la parole est déjà prise.");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        speechLock.unlock();
                    }
                }
            }).start();
        }
        System.out.println(count + " buzzers initialisés.");
    }

    private void triggerBuzz(int id) {
        Buzzer buzzer = buzzers.get(id);
        if (buzzer == null) {
            System.out.println("Buzzer " + id + " non trouvé.");
            return;
        }

        speechLock.lock();
        try {
            buzzer.getCondition().signal();
        } finally {
            speechLock.unlock();
        }
    }

    private void simulateAllBuzzes() {
        if (buzzers.isEmpty()) {
            System.out.println("Aucun buzzer initialisé.");
            return;
        }

        speechLock.lock();
        try {
            for (Buzzer buzzer : buzzers.values()) {
                buzzer.getCondition().signal();
            }
        } finally {
            speechLock.unlock();
        }
    }

    private void listBuzzers() {
        if (buzzers.isEmpty()) {
            System.out.println("Aucun buzzer actif.");
            return;
        }
        buzzers.forEach((id, b) -> System.out.println("Buzzer " + id));
    }

    private void resetGame() {
        responseTimer.cancel();
        responseTimer = new Timer();
        isSpeaking = false;
        System.out.println("🔁 Système réinitialisé. Prêt pour une nouvelle question.");
    }
}
