package com.somsinha.pokertracker.repository;

import com.somsinha.pokertracker.model.Game;
import com.somsinha.pokertracker.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, UUID> {
  List<Game> findByFinished(boolean finished);

  List<Game> findByDateFinishedAfter(LocalDateTime dateFinishedAfter);

  List<Game> findByCreatedBy(User user);

  List<Game> findByFinishedAndCreatedBy(boolean finished, User user);
}
