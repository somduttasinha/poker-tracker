package com.somsinha.pokertracker.service;

import com.somsinha.pokertracker.model.Game;
import com.somsinha.pokertracker.model.User;
import com.somsinha.pokertracker.repository.GameRepository;
import com.somsinha.pokertracker.repository.UserRepository;
import com.somsinha.pokertracker.utils.SecurityUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

  private final GameRepository gameRepository;
  private final UserRepository userRepository;

  public List<Game> getAllGames(Optional<String> keycloakId) {
    if (keycloakId.isPresent()) {
      User user = userRepository.findByKeycloakId(keycloakId.get()).get();
      return gameRepository.findByCreatedBy(user);
    }
    return gameRepository.findAll();
  }

  public List<Game> getAllActiveGames(Optional<String> keycloakId) {
    if (keycloakId.isPresent()) {
      User user = userRepository.findByKeycloakId(keycloakId.get()).get();
      return gameRepository.findByFinishedAndCreatedBy(false, user);
    }

    return gameRepository.findByFinished(false);
  }

  public Game createGame(Game game) {

    String keycloakId = SecurityUtils.getCurrentKeycloakId();
    String username = SecurityUtils.getCurrentUsername();

    User user =
        userRepository
            .findByKeycloakId(keycloakId)
            .orElseGet(
                () -> {
                  User newUser = new User();
                  newUser.setKeycloakId(keycloakId);
                  newUser.setPreferredUsername(username);
                  return userRepository.save(newUser);
                });

    game.setDateCreated(LocalDateTime.now());
    game.setCreatedBy(user);

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
