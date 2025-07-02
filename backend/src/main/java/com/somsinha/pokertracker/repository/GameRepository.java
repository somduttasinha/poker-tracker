package com.somsinha.pokertracker.repository;

import com.somsinha.pokertracker.model.Game;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, UUID> {}
