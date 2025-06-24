package com.buzzroom.controller;

import com.buzzroom.model.Player;
import com.buzzroom.service.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/buzz/{id}")
    public String buzz(@PathVariable int id) {
        return gameService.handleBuzz(id);
    }

    @PostMapping("/validate/{id}")
    public String validate(@PathVariable int id) {
        return gameService.validateAnswer(id);
    }

    @PostMapping("/reset")
    public String reset() {
        return gameService.reset();
    }

    @GetMapping("/scores")
    public List<Player> scores() {
        return gameService.getScores();
    }
}
