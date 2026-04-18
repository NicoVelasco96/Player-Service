package com.tournament.player.service;

import com.tournament.player.client.SteamAPIClient;
import com.tournament.player.dto.PlayerDTO;
import com.tournament.player.model.Player;
import com.tournament.player.repository.IPlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PlayerService implements IPlayerService {

    @Autowired
    private IPlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SteamAPIClient steamApiClient;

    @Override
    @Transactional
    public PlayerDTO.PlayerResponse register(PlayerDTO.RegisterRequest request) {
        if (playerRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username en uso: " + request.getUsername());
        }
        if (playerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email en uso: " + request.getEmail());
        }

        Player player = Player.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        if (request.getSteamId() != null && !request.getSteamId().isBlank()) {
            enrichWithSteam(player, request.getSteamId());
        }

        Player saved = playerRepository.save(player);
        log.info("Player registrado: {}", saved.getUsername());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public PlayerDTO.PlayerResponse linkSteamAccount(Long playerId, String steamId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player no encontrado: " + playerId));

        if (playerRepository.existsBySteamId(steamId)) {
            throw new IllegalArgumentException("Steam ya vinculado a otro jugador");
        }

        enrichWithSteam(player, steamId);
        return toResponse(playerRepository.save(player));
    }

    @Override
    public PlayerDTO.PlayerResponse getById(Long id) {
        return playerRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Player no encontrado: " + id));
    }

    @Override
    public PlayerDTO.PlayerResponse getByUsername(String username) {
        return playerRepository.findByUsername(username)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Player no encontrado: " + username));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void enrichWithSteam(Player player, String steamId) {
        player.setSteamId(steamId);
        steamApiClient.getPlayerSumary(steamId)
                .subscribe(profile -> {
                    player.setSteamDisplayName(profile.getDisplayName());
                    player.setSteamAvatarUrl(profile.getAvatarUrl());
                    playerRepository.save(player);
                });
    }

    private PlayerDTO.PlayerResponse toResponse(Player p) {
        PlayerDTO.PlayerResponse res = new PlayerDTO.PlayerResponse();
        res.setId(p.getId());
        res.setUsername(p.getUsername());
        res.setEmail(p.getEmail());
        res.setSteamId(p.getSteamId());
        res.setSteamDisplayName(p.getSteamDisplayName());
        res.setSteamAvatarUrl(p.getSteamAvatarUrl());
        res.setSteamLevel(p.getSteamLevel());
        res.setStatus(p.getStatus());
        res.setCreatedAt(p.getCreatedAt());
        return res;
    }
}