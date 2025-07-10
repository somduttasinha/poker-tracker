package com.somsinha.pokertracker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
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
public class BuyIn {

  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
  private UUID id;

  @ManyToOne(optional = false)
  private Player player;

  private BigDecimal amount;

  private LocalDateTime timestamp;
}
