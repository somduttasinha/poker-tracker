package com.somsinha.pokertracker.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.somsinha.pokertracker.model.Game;
import com.somsinha.pokertracker.model.Player;
import com.somsinha.pokertracker.model.Stack;
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
class StackControllerTest {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private GameRepository gameRepository;

  @Autowired private PlayerRepository playerRepository;

  @Test
  void shouldCreateAndFetchStack() throws Exception {

    Game game =
        gameRepository.save(
            Game.builder().name("Test Game").dateCreated(LocalDateTime.now()).build());

    Player player = playerRepository.save(Player.builder().name("Test Player").game(game).build());

    BigDecimal stack = new BigDecimal("150.0");

    String response =
        mockMvc
            .perform(
                post("/api/players/" + player.getId() + "/stack")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(stack)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Stack saved = objectMapper.readValue(response, Stack.class);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getPlayer().getId()).isEqualTo(player.getId());
    assertThat(saved.getFinalAmount()).isEqualByComparingTo("150.0");

    String getResponse =
        mockMvc
            .perform(get("/api/players/" + player.getId() + "/stack"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Stack fetched = objectMapper.readValue(getResponse, Stack.class);

    assertThat(fetched.getId()).isEqualTo(saved.getId());
    assertThat(fetched.getFinalAmount()).isEqualByComparingTo("150.0");
  }
}
