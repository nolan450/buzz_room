package com.buzzroom.controller;

import com.buzzroom.model.Player;
import com.buzzroom.service.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    // ne sert pas
    @PostMapping("/buzz/{id}")
    public String buzz(@PathVariable int id) {
        System.out.println("Buzz from player " + id);
        return gameService.handleBuzz(id);
    }

    @PostMapping("/validate/{id}")
    public String validate(@PathVariable int id) {
        System.out.println("Validate answer for player " + id);
        return gameService.validateAnswer(id);
    }

    @PostMapping("/penalize/{id}")
    public String penalize(@PathVariable int id) {
        System.out.println("Penalize player " + id);
        return gameService.penalize(id);
    }

    // ne sert pas
    @PostMapping("/reset")
    public String reset() {
        System.out.println("Resetting game state");
        return gameService.reset();
    }

    // ne sert pas
    @GetMapping("/scores")
    public List<Player> scores() {
        return gameService.getScores();
    }
}
