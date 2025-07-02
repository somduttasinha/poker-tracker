package com.somsinha.pokertracker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {

  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
  private UUID id;

  private String name;

  @ManyToOne(optional = false)
  @JoinColumn(name = "game_id", nullable = false)
  private Game game;
}
