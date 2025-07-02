package com.somsinha.pokertracker.service;

import com.somsinha.pokertracker.dto.PlayerResultDTO;
import com.somsinha.pokertracker.dto.SettlementDTO;
import com.somsinha.pokertracker.model.BuyIn;
import com.somsinha.pokertracker.model.Game;
import com.somsinha.pokertracker.model.Player;
import com.somsinha.pokertracker.model.Stack;
import com.somsinha.pokertracker.repository.BuyInRepository;
import com.somsinha.pokertracker.repository.PlayerRepository;
import com.somsinha.pokertracker.repository.StackRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class GameSummaryService {

  private final PlayerRepository playerRepository;
  private final BuyInRepository buyInRepository;
  private final StackRepository stackRepository;

  public GameSummaryService(PlayerRepository playerRepository, BuyInRepository buyInRepository,
      StackRepository stackRepository) {
    this.playerRepository = playerRepository;
    this.buyInRepository = buyInRepository;
    this.stackRepository = stackRepository;
  }

  public List<PlayerResultDTO> getPlayerResults(Game game) {
    List<Player> players = playerRepository.findByGame(game);

    return players.stream().map(player -> {
      BigDecimal totalBuyIn = buyInRepository.findByPlayer(player).stream()
          .map(BuyIn::getAmount)
          .reduce(BigDecimal.ZERO, BigDecimal::add);

      BigDecimal finalStack = stackRepository.findByPlayer(player)
          .map(Stack::getFinalAmount)
          .orElse(BigDecimal.ZERO);

      BigDecimal net = finalStack.subtract(totalBuyIn);
      return new PlayerResultDTO(player.getId(), player.getName(), totalBuyIn, finalStack, net);
    }).collect(Collectors.toList());
  }

  public List<SettlementDTO> calculateSettlements(List<PlayerResultDTO> playerResults) {
    List<PlayerResultDTO> owes = new ArrayList<>();
    List<PlayerResultDTO> gets = new ArrayList<>();

    for (PlayerResultDTO r : playerResults) {
      if (r.getNetResult().compareTo(BigDecimal.ZERO) < 0) {
        owes.add(new PlayerResultDTO(r.getPlayerId(), r.getPlayerName(), r.getTotalBuyIn(),
            r.getFinalStack(), r.getNetResult().abs()));
      } else {
        gets.add(new PlayerResultDTO(r.getPlayerId(), r.getPlayerName(), r.getTotalBuyIn(),
            r.getFinalStack(), r.getNetResult()));
      }
    }

    List<SettlementDTO> settlements = new ArrayList<>();

    int i = 0, j = 0;
    while (i < owes.size() && j < gets.size()) {
      PlayerResultDTO debtor = owes.get(i);
      PlayerResultDTO creditor = gets.get(j);

      BigDecimal amount = debtor.getNetResult().min(creditor.getNetResult());

      settlements.add(new SettlementDTO(
          debtor.getPlayerId(),
          creditor.getPlayerId(),
          amount
      ));

      debtor.setNetResult(debtor.getNetResult().subtract(amount));
      creditor.setNetResult(creditor.getNetResult().subtract(amount));

      if (debtor.getNetResult().compareTo(BigDecimal.ZERO) == 0) i++;
      if (creditor.getNetResult().compareTo(BigDecimal.ZERO) == 0) j++;
    }

    return settlements;
  }

}
