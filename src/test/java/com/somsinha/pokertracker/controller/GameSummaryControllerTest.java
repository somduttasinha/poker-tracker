package com.somsinha.pokertracker.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.somsinha.pokertracker.dto.PlayerResultDTO;
import com.somsinha.pokertracker.dto.SettlementDTO;
import com.somsinha.pokertracker.model.BuyIn;
import com.somsinha.pokertracker.model.Game;
import com.somsinha.pokertracker.model.Player;
import com.somsinha.pokertracker.model.Stack;
import com.somsinha.pokertracker.repository.BuyInRepository;
import com.somsinha.pokertracker.repository.GameRepository;
import com.somsinha.pokertracker.repository.PlayerRepository;
import com.somsinha.pokertracker.repository.StackRepository;
import com.somsinha.pokertracker.service.GameSummaryService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class GameSummaryControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private GameRepository gameRepository;

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private BuyInRepository buyInRepository;

  @Autowired
  private StackRepository stackRepository;

  @Test
  void shouldReturnCorrectSummaryForGame() throws Exception {
    Game game = gameRepository.save(Game.builder()
        .name("Integration Test Game")
        .dateCreated(LocalDateTime.now())
        .build());

    Player alice = playerRepository.save(Player.builder().name("Alice").game(game).build());
    Player bob = playerRepository.save(Player.builder().name("Bob").game(game).build());

    buyInRepository.saveAll(List.of(
        BuyIn.builder().player(alice).amount(BigDecimal.valueOf(20)).build(),
        BuyIn.builder().player(bob).amount(BigDecimal.valueOf(30)).build()
    ));

    stackRepository.saveAll(List.of(
        Stack.builder().player(alice).finalAmount(BigDecimal.valueOf(50)).build(),
        Stack.builder().player(bob).finalAmount(BigDecimal.valueOf(0)).build()
    ));

    String json = mockMvc.perform(get("/games/" + game.getId() + "/summary")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    Map<?, ?> response = objectMapper.readValue(json, Map.class);
    List<?> playerResults = (List<?>) response.get("results");
    List<?> settlements = (List<?>) response.get("settlements");

    assertThat(playerResults).hasSize(2);

    Map<String, Object> aliceResult = (Map<String, Object>) playerResults.stream()
        .filter(p -> ((String) ((Map<?, ?>) p).get("playerName")).equals("Alice"))
        .findFirst()
        .orElseThrow();

    Map<String, Object> bobResult = (Map<String, Object>) playerResults.stream()
        .filter(p -> ((String) ((Map<?, ?>) p).get("playerName")).equals("Bob"))
        .findFirst()
        .orElseThrow();

    assertThat(aliceResult.get("netResult")).isEqualTo(30.0); // 50 - 20
    assertThat(bobResult.get("netResult")).isEqualTo(-30.0);  // 0 - 30

    assertThat(settlements).hasSize(1);
    Map<String, Object> settlement = (Map<String, Object>) settlements.get(0);

    UUID fromPlayerId = UUID.fromString((String) settlement.get("fromPlayerId"));
    UUID toPlayerId = UUID.fromString((String) settlement.get("toPlayerId"));

    assertThat(fromPlayerId).isEqualTo(bob.getId());
    assertThat(toPlayerId).isEqualTo(alice.getId());
    assertThat(settlement.get("amount")).isEqualTo(30.0);

  }

}
