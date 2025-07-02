package com.somsinha.pokertracker.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SettlementDTO {
  private UUID fromPlayerId;
  private UUID toPlayerId;
  private BigDecimal amount;
}
