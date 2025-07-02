package com.somsinha.pokertracker.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerResultDTO {
  private UUID playerId;
  private String playerName;
  private BigDecimal totalBuyIn;
  private BigDecimal finalStack;
  private BigDecimal netResult;
}
