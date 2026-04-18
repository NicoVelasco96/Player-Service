package com.tournament.player.dto;

import com.tournament.player.model.PlayerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

public class PlayerDTO {

    @Data
    public static class RegisterRequest {
        @NotBlank
        @Size(min = 3, max = 30)
        private String username;

        @NotBlank
        @Email
        private String email;

        @NotBlank
        @Size(min = 8)
        private String password;

        private String steamId; // opcional
    }

    @Data
    public static class LoginRequest {
        @NotBlank
        private String username;

        @NotBlank
        private String password;
    }

    @Data
    public static class LinkSteamRequest {
        @NotBlank
        private String steamId;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private String tokenType = "Bearer";
        private PlayerResponse player;

        public AuthResponse(String token, PlayerResponse player) {
            this.token = token;
            this.player = player;
        }
    }

    @Data
    public static class PlayerResponse {
        private Long id;
        private String username;
        private String email;
        private String steamId;
        private String steamDisplayName;
        private String steamAvatarUrl;
        private Integer steamLevel;
        private String steamProfileUrl;
        private PlayerStatus status;
        private LocalDateTime createdAt;
    }

    @Data
    public static class SteamProfileResponse {
        private String steamId;
        private String displayName;
        private String avatarUrl;
        private String profileUrl;
        private Integer level;
    }
}
