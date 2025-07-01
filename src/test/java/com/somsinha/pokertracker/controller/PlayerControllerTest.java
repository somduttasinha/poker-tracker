package com.somsinha.pokertracker.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.somsinha.pokertracker.dto.PlayerRequest;
import com.somsinha.pokertracker.model.Game;
import com.somsinha.pokertracker.model.Player;
import com.somsinha.pokertracker.repository.GameRepository;
import com.somsinha.pokertracker.repository.PlayerRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PlayerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private GameRepository gameRepository;

  @Test
  void shouldAddPlayerToGame() throws Exception {
    Game game =
        gameRepository.save(Game.builder().name("Test Game").dateCreated(LocalDateTime.now())
            .build());

    PlayerRequest request = new PlayerRequest("Test Player");

    String response = mockMvc.perform(post("/games/{gameId}/players", game.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();



    Player saved = objectMapper.readValue(response, Player.class);
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getName()).isEqualTo("Test Player");
    assertThat(saved.getGame().getId()).isEqualTo(game.getId());
  }

}
