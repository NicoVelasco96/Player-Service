package com.tournament.player.service;

import com.tournament.player.dto.PlayerDTO;

public interface IPlayerService {
    public PlayerDTO.PlayerResponse register(PlayerDTO.RegisterRequest request);
    public PlayerDTO.PlayerResponse linkSteamAccount(Long playerId, String steamId);
    public PlayerDTO.PlayerResponse getById(Long id);
    public PlayerDTO.PlayerResponse getByUsername(String username);
}
