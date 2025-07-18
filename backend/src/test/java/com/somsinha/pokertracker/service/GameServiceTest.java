package com.somsinha.pokertracker.service;

import static org.junit.jupiter.api.Assertions.*;

import com.somsinha.pokertracker.model.Game;
import com.somsinha.pokertracker.repository.GameRepository;
import com.somsinha.pokertracker.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GameServiceTest {

  @Autowired private GameRepository gameRepository;
  @Autowired private UserRepository userRepository;

  private GameService gameService;

  /**
   * Test for getAllGames method. Some games have finished after yesterday, some before. This
   * function should only return the ones that finished after yesterday. We will save games in the
   * H2 database.
   */
  @BeforeEach
  void setUp() {
    // put some games in the H2 database
    gameRepository.deleteAll(); // Clear the repository before each test
    gameRepository.saveAll(
        List.of(
            Game.builder()
                .name("Game 1")
                .dateCreated(LocalDateTime.now().minusDays(1))
                .finished(true)
                .dateFinished(LocalDateTime.now().minusHours(23))
                .build(),
            Game.builder()
                .name("Game 2")
                .dateCreated(LocalDateTime.now().minusDays(1))
                .finished(false)
                .build(),
            Game.builder()
                .name("Game 3")
                .dateCreated(LocalDateTime.now().minusHours(5))
                .finished(true)
                .dateFinished(LocalDateTime.now().minusHours(3))
                .build(),
            Game.builder()
                .name("Game 4")
                .dateCreated(LocalDateTime.now().minusDays(3))
                .finished(false)
                .build()));

    gameService = new GameService(gameRepository, userRepository);
  }

  @AfterEach
  void tearDown() {
    gameRepository.deleteAll();
  }

  @Test
  void getFinishedGamesSinceTest() {
    LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

    List<Game> allGames = gameService.getAllGames();
    List<Game> finishedGames = gameService.getFinishedGamesSince(yesterday);

    // sanity check, make sure we have 4 games in the database
    assertEquals(4, gameRepository.findAll().size(), "Should have 4 games in the database");

    List<Game> expected =
        allGames.stream()
            .filter(Game::isFinished)
            .filter(game -> game.getDateFinished().isAfter(yesterday))
            .toList();

    assertEquals(
        expected.size(), finishedGames.size(), "Should return 2 finished games since yesterday");
    assertTrue(finishedGames.stream().anyMatch(game -> game.getName().equals("Game 1")));
    assertTrue(finishedGames.stream().anyMatch(game -> game.getName().equals("Game 3")));
  }
}
