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
        Game game = gameRepository
                .findById(id)
                .map(
                        g -> {
                            g.setFinished(true);
                            return gameRepository.save(g);
                        })
                .orElseThrow(() -> new RuntimeException("Game not found")); // TODO:
        // create custom exception
        return game;
    }

    public Game getGame(UUID id) {
        return gameRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found")); // TODO:
        // create custom exception
    }
}
