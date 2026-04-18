package com.tournament.player.controller;

import com.tournament.player.dto.PlayerDTO;
import com.tournament.player.model.Player;
import com.tournament.player.repository.IPlayerRepository;
import com.tournament.player.service.JWTService;
import com.tournament.player.service.IPlayerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/players")
public class PlayerController {

    @Autowired
    private IPlayerService playerService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private IPlayerRepository playerRepository;

    @PostMapping("/register")
    public ResponseEntity<PlayerDTO.PlayerResponse> register(
            @Valid @RequestBody PlayerDTO.RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(playerService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<PlayerDTO.AuthResponse> login(
            @Valid @RequestBody PlayerDTO.LoginRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword())
        );

        Player player = playerRepository.findByUsername(auth.getName())
                .orElseThrow();

        String token = jwtService.generateToken(player.getUsername(), player.getId());
        return ResponseEntity.ok(
                new PlayerDTO.AuthResponse(token, playerService.getByUsername(player.getUsername()))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO.PlayerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.getById(id));
    }

    @PostMapping("/{id}/steam")
    public ResponseEntity<PlayerDTO.PlayerResponse> linkSteam(
            @PathVariable Long id,
            @Valid @RequestBody PlayerDTO.LinkSteamRequest request) {
        return ResponseEntity.ok(
                playerService.linkSteamAccount(id, request.getSteamId())
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}