package com.somsinha.pokertracker.repository;

import com.somsinha.pokertracker.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
  public Optional<User> findByKeycloakId(String keycloakId);
}
