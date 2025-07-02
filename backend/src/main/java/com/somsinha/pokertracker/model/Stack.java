package com.somsinha.pokertracker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
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
public class Stack {

  @Id
  @GeneratedValue
  private UUID id;

  @OneToOne(optional = false)
  @JoinColumn(name = "player_id", nullable = false, unique = true)
  private Player player;

  private BigDecimal finalAmount;
}
