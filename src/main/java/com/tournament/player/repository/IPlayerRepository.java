package com.tournament.player.repository;

import com.tournament.player.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IPlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByUsername(String username);
    Optional<Player> findByEmail(String email);
    Optional<Player> findBySteamId(String steamId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsBySteamId(String steamId);
}
