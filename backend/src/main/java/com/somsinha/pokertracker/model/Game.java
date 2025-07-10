package com.somsinha.pokertracker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
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
public class Game {
  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
  private UUID id;

  private String name;

  private LocalDateTime dateCreated;

  private boolean finished;
}
