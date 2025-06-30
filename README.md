# Buzz Room

Buzz Room est une application de quiz en temps réel avec gestion de buzzers via MQTT, interface maître du jeu en web, et gestion centralisée des scores par une API Spring Boot. Elle se compose de trois modules principaux :

* Un CLI (simulateur de buzzers)
* Une API Spring Boot (coeur logique et synchronisation)
* Une IHM Web (interface maître de jeu)

## Fonctionnalités

* Enregistrement dynamique des joueurs via MQTT
* Système de buzz concurrent géré par mutex et timer
* Gestion des scores (bonne ou mauvaise réponse)
* Communication en temps réel avec le front via Server-Sent Events (SSE)
* Interface web permettant de piloter le jeu (scores, validation, reset)

## Architecture

```
[ CLI / Simulateur ] <-- MQTT --> [ API Spring Boot ] <-- SSE --> [ Front Web React ou HTML ]
```

* Les buzzers émettent des événements sur MQTT (topics `buzzroom/buzz`, `buzzroom/register`)
* L’API écoute ces événements, applique les règles de jeu, et informe le front en SSE
* Le front affiche les mises à jour en temps réel (buzz, scores, validation…)

## Lancer le projet

### 1. Prérequis

* Java 17+
* Maven
* Un broker MQTT (ex : HiveMQ Cloud)
* (Optionnel) Node.js si tu utilises un front React

### 2. Configuration MQTT

Dans le fichier `MqttSubscriber.java`, renseigner les identifiants HiveMQ :

```java
options.setUserName("...");
options.setPassword("...".toCharArray());
```

### 3. Lancer l’API

```bash
./mvnw spring-boot:run
```

L’API démarre sur le port `8080`.

## Commandes du CLI

Le CLI simule les buzzers. Voici les commandes principales :

```bash
> init <nombre_buzzers>  # Initialise n buzzers
> buzz <id>              # Le buzzer id buzz
> score <id> <+/-points> # Ajoute ou retire des points
> reset                  # Réinitialise les buzzers
> scores                 # Affiche les scores
> exit                   # Quitte
```

## API REST

| Endpoint         | Méthode | Description                    |
| ---------------- | ------- | ------------------------------ |
| `/buzz/{id}`     | POST    | Simule un buzz depuis le front |
| `/validate/{id}` | POST    | Valide la réponse d’un joueur  |
| `/reset`         | POST    | Réinitialise le tour           |
| `/scores`        | GET     | Récupère la liste des scores   |
| `/sse/events`    | GET     | Flux SSE pour le front         |

## Événements SSE

Le front écoute sur `/sse/events`. Les événements possibles :

* `register:n` → n joueurs inscrits
* `buzz:id` → le joueur id a buzzé
* `valid:id` → le joueur id a été validé
* `reset` → tour réinitialisé

## Gestion du temps

La gestion du chrono est faite côté API. Lorsqu’un joueur buzz, un timer de 10 secondes démarre. Pendant cette période :

* Le joueur est actif (bloque les autres buzz)
* Après 10 secondes, l’API réinitialise l’état (`buzzLocked = false`)

Cela évite qu’un même joueur buzz trop longtemps ou plusieurs à la fois.

## Fichiers importants

* `GameService.java` : logique métier et score
* `MqttSubscriber.java` : souscription aux topics MQTT
* `SseController.java` : gestion des SSE vers le front
* `script.js` : interactions front → API
* `CLI.java` : simulateur de buzzers

## To Do

* Ajouter persistance des scores (BDD, JSON…)
* Améliorer la gestion du temps par question
* Gérer plusieurs parties ou sessions
* Version mobile des buzzers
