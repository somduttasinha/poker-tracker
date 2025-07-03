package com.somsinha.pokertracker.controller;

import com.somsinha.pokertracker.dto.PlayerRequest;
import com.somsinha.pokertracker.model.Game;
import com.somsinha.pokertracker.model.Player;
import com.somsinha.pokertracker.repository.GameRepository;
import com.somsinha.pokertracker.repository.PlayerRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/games/{gameId}/players")
public class PlayerController {

  private final PlayerRepository playerRepository;
  private final GameRepository gameRepository;

  public PlayerController(PlayerRepository playerRepository,
      GameRepository gameRepository) {
    this.playerRepository = playerRepository;
    this.gameRepository = gameRepository;
  }

  @PostMapping
  public ResponseEntity<Player> addPlayer(
      @PathVariable UUID gameId, @RequestBody PlayerRequest request
  ) {
    Game game = gameRepository.findById(gameId).orElseThrow(
        () -> new IllegalArgumentException("Game not found")
    );

    Player player = Player.builder().name(request.name()).game(game).build();

    player.setGame(game);
    Player saved = playerRepository.save(player);
    return ResponseEntity.ok(saved);
  }

  @GetMapping
  public ResponseEntity<List<Player>> getPlayers(@PathVariable UUID gameId) {
    Game game = gameRepository.findById(gameId).orElseThrow(() -> new IllegalArgumentException(
        "Game not found")
    );

    return ResponseEntity.ok(playerRepository.findByGame(game));
  }
}
