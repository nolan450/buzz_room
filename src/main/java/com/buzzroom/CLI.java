package com.buzzroom;

import java.util.*;

public class CLI {

    private final Map<Integer, Buzzer> buzzers = new HashMap<>();
    private final Scoreboard scoreboard = new Scoreboard();

    private int reactivityMs = 10; // délai par défaut pour la gestion de simultanéité
    private Random random = new Random();

    // Pour la gestion des buzzers actifs
    private Integer activePlayerId = null;
    private Timer responseTimer = new Timer();
    private boolean buzzLocked = false;


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
                        simulateBuzz(Integer.parseInt(args[1]));
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
        System.out.println("- init             : initialise n buzzers");
        System.out.println("- buzz <id>        : simule un buzz du buzzer");
        System.out.println("- buzz-all         : simule tous les buzzers avec délai");
        System.out.println("- list             : liste les buzzers actifs");
        System.out.println("- set-reactivity <ms> : délai entre les buzz simultanés");
        System.out.println("- score <id> <+/-points> : ajoute ou retire des points");
        System.out.println("- scores                 : affiche les scores de tous les joueurs");
        System.out.println("- reset            : réinitialise le tour (débloque les buzzers)");
        System.out.println("- exit             : quitter");
    }

    private void initBuzzers(int count) {
        buzzers.clear();
        scoreboard.clear();
        for (int i = 1; i <= count; i++) {
             // entre 0 et reactivityMs
            buzzers.put(i, new Buzzer(i));
            scoreboard.addPlayer(i);
        }
        System.out.println(count + " buzzers initialisés.");
    }

    private void simulateBuzz(int id) {
        if (buzzLocked) {
            System.out.println("Un joueur est déjà en train de répondre. Attente de réinitialisation.");
            return;
        }

        Buzzer b = buzzers.get(id);
        if (b == null) {
            System.out.println("Buzzer " + id + " non trouvé.");
            return;
        }

        int latency = random.nextInt(reactivityMs);
        long timestamp = System.currentTimeMillis();

        System.out.println("Buzzer " + id + " a buzzé avec délai " + latency + " ms (timestamp = " + timestamp + ")");

        // Activer le joueur
        activePlayerId = id;
        buzzLocked = true;

        System.out.println("⏳ Le joueur " + id + " a 10 secondes pour répondre...");

        // Lancer le chrono
        responseTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("🔔 Temps écoulé pour le joueur " + id + " !");
                System.out.println("Veuillez valider ou refuser la réponse.");
            }
        }, 10_000); // 10 secondes
    }

    private void simulateAllBuzzes() {
        if (buzzers.isEmpty()) {
            System.out.println("Aucun buzzer initialisé.");
            return;
        }

        Map<Integer, Long> results = new HashMap<>();
        for (Buzzer b : buzzers.values()) {
            results.put(b.getId(), System.currentTimeMillis());
        }

        results.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(e -> System.out.println("Buzzer " + e.getKey() + " a buzzé à " + e.getValue() + " ms"));

        int winner = results.entrySet().stream().min(Map.Entry.comparingByValue()).get().getKey();
        System.out.println("🏆 Le premier à buzz : Buzzer " + winner);
    }

    private void listBuzzers() {
        if (buzzers.isEmpty()) {
            System.out.println("Aucun buzzer actif.");
            return;
        }
        buzzers.forEach((id, b) -> System.out.println("Buzzer " + id));
    }

    private void resetGame() {
        activePlayerId = null;
        buzzLocked = false;
        responseTimer.cancel();
        responseTimer = new Timer();
        System.out.println("🔁 Système réinitialisé. Prêt pour une nouvelle question.");
    }

}
