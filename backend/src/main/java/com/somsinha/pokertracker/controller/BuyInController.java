package com.somsinha.pokertracker.controller;

import com.somsinha.pokertracker.model.BuyIn;
import com.somsinha.pokertracker.model.Player;
import com.somsinha.pokertracker.repository.BuyInRepository;
import com.somsinha.pokertracker.repository.PlayerRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@RequestMapping("/api/players/{playerId}/buyins")
public class BuyInController {

  private final BuyInRepository buyInRepository;
  private final PlayerRepository playerRepository;

  public BuyInController(BuyInRepository buyInRepository, PlayerRepository playerRepository) {
    this.buyInRepository = buyInRepository;
    this.playerRepository = playerRepository;
  }

  @PostMapping
  public ResponseEntity<BuyIn> addBuyIn(
      @PathVariable UUID playerId,
      @RequestBody BigDecimal amount
  ) {
    Player player = playerRepository.findById(playerId)
        .orElseThrow(() -> new IllegalArgumentException("Player not found"));

    BuyIn buyIn =
        BuyIn.builder().player(player).amount(amount).timestamp(LocalDateTime.now()).build();

    return ResponseEntity.ok(buyInRepository.save(buyIn));
  }

  @GetMapping
  public ResponseEntity<List<BuyIn>> getBuyIns(@PathVariable UUID playerId) {
    Player player = playerRepository.findById(playerId)
        .orElseThrow(() -> new IllegalArgumentException("Player not found"));

    return ResponseEntity.ok(buyInRepository.findByPlayer(player));
  }

}