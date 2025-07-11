package com.somsinha.pokertracker.service;

import com.somsinha.pokertracker.model.Game;
import com.somsinha.pokertracker.repository.GameRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GameService {

  private GameRepository gameRepository;

  public GameService(GameRepository gameRepository) {
    this.gameRepository = gameRepository;
  }

  public List<Game> getAllGames() {
    return gameRepository.findAll();
  }

  public List<Game> getAllActiveGames() {
    return gameRepository.findByFinished(false);
  }

  public Game createGame(Game game) {
    game.setDateCreated(LocalDateTime.now());
    Game saved = gameRepository.save(game);
    return saved;
  }

  public Game endGame(UUID id) {
    // TODO:
    // create custom exception
    return gameRepository
        .findById(id)
        .map(
            g -> {
              g.setFinished(true);
              if (g.getDateFinished() == null) {
                g.setDateFinished(LocalDateTime.now());
              }
              return gameRepository.save(g);
            })
        .orElseThrow(() -> new RuntimeException("Game not found"));
  }

  public Game getGame(UUID id) {
    return gameRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("Game not found")); // TODO:
    // create custom exception
  }

  public List<Game> getFinishedGamesSince(LocalDateTime date) {
    return gameRepository.findByDateFinishedAfter(date);
  }
}
