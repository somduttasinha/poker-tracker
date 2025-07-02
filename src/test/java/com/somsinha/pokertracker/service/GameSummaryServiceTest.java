package com.somsinha.pokertracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.somsinha.pokertracker.dto.PlayerResultDTO;
import com.somsinha.pokertracker.dto.SettlementDTO;
import com.somsinha.pokertracker.model.BuyIn;
import com.somsinha.pokertracker.model.Game;
import com.somsinha.pokertracker.model.Player;
import com.somsinha.pokertracker.model.Stack;
import com.somsinha.pokertracker.repository.BuyInRepository;
import com.somsinha.pokertracker.repository.PlayerRepository;
import com.somsinha.pokertracker.repository.StackRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GameSummaryServiceTest {

  @Mock
  private PlayerRepository playerRepository;

  @Mock
  private BuyInRepository buyInRepository;

  @Mock
  private StackRepository stackRepository;

  @InjectMocks
  private GameSummaryService summaryService;

  private Game game;
  private Player alice, bob;

  @BeforeEach
  void setUp() {
    game = Game.builder().id(UUID.randomUUID()).name("Heads up poker night").build();
    alice = Player.builder().id(UUID.randomUUID()).name("Alice").game(game).build();
    bob = Player.builder().id(UUID.randomUUID()).name("Bob").game(game).build();
  }

  @Test
  void shouldCalculatePlayerResultsCorrectly() {
    when(playerRepository.findByGame(game)).thenReturn(List.of(alice, bob));

    when(buyInRepository.findByPlayer(alice)).thenReturn(List.of(
        BuyIn.builder().amount(BigDecimal.valueOf(20)).build(),
        BuyIn.builder().amount(BigDecimal.valueOf(10)).build()
    ));
    when(buyInRepository.findByPlayer(bob)).thenReturn(List.of(
        BuyIn.builder().amount(BigDecimal.valueOf(30)).build()
    ));

    when(stackRepository.findByPlayer(alice)).thenReturn(Optional.of(
        Stack.builder().finalAmount(BigDecimal.valueOf(50)).build()
    ));
    when(stackRepository.findByPlayer(bob)).thenReturn(Optional.of(
        Stack.builder().finalAmount(BigDecimal.valueOf(10)).build()
    ));

    List<PlayerResultDTO> results = summaryService.getPlayerResults(game);

    assertThat(results).hasSize(2);
    PlayerResultDTO aliceResult = results.stream().filter(r -> r.getPlayerId().equals(alice.getId())).findFirst().orElseThrow();
    PlayerResultDTO bobResult = results.stream().filter(r -> r.getPlayerId().equals(bob.getId())).findFirst().orElseThrow();

    assertThat(aliceResult.getTotalBuyIn()).isEqualByComparingTo("30");
    assertThat(aliceResult.getFinalStack()).isEqualByComparingTo("50");
    assertThat(aliceResult.getNetResult()).isEqualByComparingTo("20");

    assertThat(bobResult.getTotalBuyIn()).isEqualByComparingTo("30");
    assertThat(bobResult.getFinalStack()).isEqualByComparingTo("10");
    assertThat(bobResult.getNetResult()).isEqualByComparingTo("-20");
  }

  @Test
  void shouldGenerateCorrectSettlements() {
    PlayerResultDTO aliceResult = new PlayerResultDTO(alice.getId(), "Alice",
        BigDecimal.valueOf(30), BigDecimal.valueOf(50), BigDecimal.valueOf(20));
    PlayerResultDTO bobResult = new PlayerResultDTO(bob.getId(), "Bob",
        BigDecimal.valueOf(30), BigDecimal.valueOf(10), BigDecimal.valueOf(-20));

    List<SettlementDTO> settlements = summaryService.calculateSettlements(List.of(aliceResult, bobResult));

    assertThat(settlements).hasSize(1);
    SettlementDTO settlement = settlements.getFirst();
    assertThat(settlement.getFromPlayerId()).isEqualTo(bob.getId());
    assertThat(settlement.getToPlayerId()).isEqualTo(alice.getId());
    assertThat(settlement.getAmount()).isEqualByComparingTo("20.0");
  }}
