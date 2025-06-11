package com.buzzroom;

import java.util.*;

public class CLI {

    private final Map<Integer, Buzzer> buzzers = new HashMap<>();
    private int reactivityMs = 10; // délai par défaut pour la gestion de simultanéité
    private Random random = new Random();

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
        System.out.println("- exit             : quitter");
    }

    private void initBuzzers(int count) {
        buzzers.clear();
        for (int i = 1; i <= count; i++) {
             // entre 0 et reactivityMs
            buzzers.put(i, new Buzzer(i));
        }
        System.out.println(count + " buzzers initialisés.");
    }

    private void simulateBuzz(int id) {
        Buzzer b = buzzers.get(id);
        if (b == null) {
            System.out.println("Buzzer " + id + " non trouvé.");
            return;
        }
        long timestamp = System.currentTimeMillis();
        
        int latency = random.nextInt(reactivityMs);
        System.out.println("Buzzer " + id + " a buzzé avec délai " + latency + " ms (timestamp = " + timestamp + ")");
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
}
