package com.somsinha.pokertracker.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.somsinha.pokertracker.model.Game;
import com.somsinha.pokertracker.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private GameRepository gameRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldCreateAndFetchGame() throws Exception {
    Game game = Game.builder().name("Test Poker Night").build();

    String response = mockMvc.perform(post("/games")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(game)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value("Test Poker Night"))
        .andReturn().getResponse().getContentAsString();

    Game createdGame = objectMapper.readValue(response, Game.class);

    mockMvc.perform(get("/games"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].id").value(createdGame.getId().toString()))
        .andExpect(jsonPath("$.[0].name").value("Test Poker Night"));


    assertThat(gameRepository.findById(createdGame.getId())).isPresent();


    }

  @Test
  void getAllGames() {
    }
}