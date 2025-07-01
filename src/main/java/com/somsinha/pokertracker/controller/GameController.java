package com.somsinha.pokertracker.controller;


import com.somsinha.pokertracker.model.Game;
import com.somsinha.pokertracker.repository.GameRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games")
public class GameController {

  private final GameRepository gameRepository;

  public GameController(GameRepository gameRepository) {
    this.gameRepository = gameRepository;
  }

  @PostMapping
  public Game createGame(@RequestBody Game game) {
    game.setDateCreated(LocalDateTime.now());
    return gameRepository.save(game);
  }

  @GetMapping
  public List<Game> getAllGames() {
    return gameRepository.findAll();
  }
}
