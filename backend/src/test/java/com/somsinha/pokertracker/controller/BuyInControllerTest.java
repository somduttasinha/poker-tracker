package com.somsinha.pokertracker.controller;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.somsinha.pokertracker.model.BuyIn;
import com.somsinha.pokertracker.model.Game;
import com.somsinha.pokertracker.model.Player;
import com.somsinha.pokertracker.repository.GameRepository;
import com.somsinha.pokertracker.repository.PlayerRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BuyInControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private GameRepository gameRepository;


  @Test
  void shouldAddAndFetchBuyInsForPlayer() throws Exception {
    Game game =
        gameRepository.save(Game.builder().name("Test Game").dateCreated(LocalDateTime.now())
            .build());

    Player player =
        playerRepository.save(Player.builder().name("Test Player").game(game).build());

    BigDecimal amountBuyIn = new BigDecimal("25.0");

    String response = mockMvc.perform(post("/players/" + player.getId() + "/buyins")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(amountBuyIn)))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    BuyIn savedBuyIn = objectMapper.readValue(response, BuyIn.class);

    assertThat(savedBuyIn.getId()).isNotNull();
    assertThat(savedBuyIn.getAmount()).isEqualByComparingTo("25.0");
    assertThat(savedBuyIn.getTimestamp()).isNotNull();
    assertThat(savedBuyIn.getPlayer().getId()).isEqualTo(player.getId());


    // Fetch BuyIns
    String listJson = mockMvc.perform(get("/players/" + player.getId() + "/buyins"))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    BuyIn[] buyIns = objectMapper.readValue(listJson, BuyIn[].class);

    assertThat(buyIns).hasSize(1);
    assertThat(buyIns[0].getAmount()).isEqualByComparingTo("25.0");


  }
}
