package com.somsinha.pokertracker.controller;

import com.somsinha.pokertracker.model.Game;
import com.somsinha.pokertracker.service.GameService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/games")
public class GameController {

  private final GameService gameService;

  public GameController(GameService gameService) {
    this.gameService = gameService;
  }

  @PostMapping
  public ResponseEntity<Game> createGame(@RequestBody Game game) {
    Game saved = gameService.createGame(game);
    return ResponseEntity.ok(saved);
  }

  @PutMapping("/end/{id}")
  public ResponseEntity<Game> endGame(@PathVariable UUID id) {
    try {
      return ResponseEntity.ok(gameService.endGame(id));
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Get all games, optionally filtered by finished status.
   *
   * @param finished if true, returns only finished games; if false or null, returns all active
   *     games.
   * @return ResponseEntity containing a list of games.
   */
  @GetMapping
  public ResponseEntity<List<Game>> getAllGames(@RequestParam(required = false) Boolean finished) {
    if (finished != null && !finished) {
      return ResponseEntity.ok(gameService.getAllGames());
    } else {
      return ResponseEntity.ok(gameService.getAllActiveGames());
    }
  }

  /**
   * Get all finished games since a specific date.
   *
   * @param since the number of hours since which to retrieve finished games.
   * @return ResponseEntity containing a list of finished games.
   */
  @GetMapping("/finished")
  public ResponseEntity<List<Game>> getFinishedGamesSince(@RequestParam("since") Integer since) {
    try {
      LocalDateTime dateTime = LocalDateTime.now().minusHours(since);
      return ResponseEntity.ok(gameService.getFinishedGamesSince(dateTime));
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<Game> getGameById(@PathVariable UUID id) {
    try {
      return ResponseEntity.ok(gameService.getGame(id));
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }
}
