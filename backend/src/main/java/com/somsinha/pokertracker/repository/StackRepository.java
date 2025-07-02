package com.somsinha.pokertracker.repository;

import com.somsinha.pokertracker.model.Player;
import com.somsinha.pokertracker.model.Stack;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StackRepository extends JpaRepository<Stack, UUID> {
  Optional<Stack> findByPlayer(Player player);
}
