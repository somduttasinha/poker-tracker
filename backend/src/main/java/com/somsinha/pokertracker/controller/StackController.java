package com.somsinha.pokertracker.controller;

import com.somsinha.pokertracker.model.Player;
import com.somsinha.pokertracker.model.Stack;
import com.somsinha.pokertracker.repository.PlayerRepository;
import com.somsinha.pokertracker.repository.StackRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/players/{playerId}/stack")
public class StackController {

  private final StackRepository stackRepository;
  private final PlayerRepository playerRepository;

  public StackController(StackRepository stackRepository, PlayerRepository playerRepository) {
    this.stackRepository = stackRepository;
    this.playerRepository = playerRepository;
  }

  @PostMapping
  public ResponseEntity<Stack> submitStack(@PathVariable UUID playerId,
      @RequestBody BigDecimal finalAmount) {
    Player player = playerRepository.findById(playerId)
        .orElseThrow(() -> new IllegalArgumentException("Player not found"));

    Stack stack = stackRepository.findByPlayer(player).map(existing -> {
      existing.setFinalAmount(finalAmount);
      return existing;
    })
        .orElse(Stack.builder().player(player).finalAmount(finalAmount).build());

    return ResponseEntity.ok(stackRepository.save(stack));
  }

  @GetMapping
  public ResponseEntity<Stack> getStack(@PathVariable UUID playerId) {
    Player player = playerRepository.findById(playerId)
        .orElseThrow(() -> new IllegalArgumentException("Player not found"));

    return stackRepository.findByPlayer(player).map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
