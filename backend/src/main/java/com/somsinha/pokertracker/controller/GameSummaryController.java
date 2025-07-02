package com.somsinha.pokertracker.controller;

import com.somsinha.pokertracker.dto.PlayerResultDTO;
import com.somsinha.pokertracker.dto.SettlementDTO;
import com.somsinha.pokertracker.model.Game;
import com.somsinha.pokertracker.repository.GameRepository;
import com.somsinha.pokertracker.service.GameSummaryService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games/{gameId}/summary")
public class GameSummaryController {

  private final GameRepository gameRepository;
  private final GameSummaryService gameSummaryService;

  public GameSummaryController(GameRepository gameRepository, GameSummaryService gameSummaryService) {
    this.gameRepository = gameRepository;
    this.gameSummaryService = gameSummaryService;
  }

  @GetMapping
  public Map<String, Object> getSummary(@PathVariable UUID gameId) {
    Game game = gameRepository.findById(gameId).orElseThrow(
        () -> new IllegalArgumentException("Game not found")
    );

    List<PlayerResultDTO> results = gameSummaryService.getPlayerResults(game);
    List<SettlementDTO> settlements = gameSummaryService.calculateSettlements(results);

    return Map.of("results", results, "settlements", settlements);
  }
}
