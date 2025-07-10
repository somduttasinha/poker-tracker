package com.somsinha.pokertracker.repository;

import com.somsinha.pokertracker.model.BuyIn;
import com.somsinha.pokertracker.model.Player;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyInRepository extends JpaRepository<BuyIn, UUID> {

  List<BuyIn> findByPlayer(Player player);
}
