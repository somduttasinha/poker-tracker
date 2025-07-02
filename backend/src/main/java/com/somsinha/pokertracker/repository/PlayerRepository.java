package com.somsinha.pokertracker.repository;

import com.somsinha.pokertracker.model.Game;
import com.somsinha.pokertracker.model.Player;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, UUID> {

  List<Player> findByGame(Game game);
}
