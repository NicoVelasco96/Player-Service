package com.tournament.player.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tournament.player.dto.PlayerDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SteamAPIClient {

    private final WebClient steamWebClient;

    @Value("${steam.api.key}")
    private String apiKey;

    public Mono<PlayerDTO.SteamProfileResponse> getPlayerSumary(String steamId) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("STEAM_API_KEY no configurado — omitiendo enriquecimiento Steam.");
            return Mono.empty();
        }

        Mono<SteamSummaryWrapper> summaryMono = steamWebClient.get()
                .uri(uri -> uri
                        .path("/ISteamUser/GetPlayerSummaries/v0002/")
                        .queryParam("key", apiKey)
                        .queryParam("steamids", steamId)
                        .build())
                .retrieve()
                .bodyToMono(SteamSummaryWrapper.class);

        Mono<Integer> levelMono = steamWebClient.get()
                .uri(uri -> uri
                        .path("/IPlayerService/GetSteamLevel/v1/")
                        .queryParam("key", apiKey)
                        .queryParam("steamid", steamId)
                        .build())
                .retrieve()
                .bodyToMono(SteamLevelWrapper.class)
                .mapNotNull(w -> w.getResponse() != null ? w.getResponse().getPlayerLevel() : null)
                .onErrorResume(ex -> {
                    log.error("Error obteniendo Steam level: {}", ex.getMessage());
                    return Mono.just(0);
                });

        return Mono.zip(summaryMono, levelMono)
                .mapNotNull(tuple -> {
                    SteamSummaryWrapper wrapper = tuple.getT1();
                    Integer level = tuple.getT2();

                    if (wrapper.getResponse() == null
                            || wrapper.getResponse().getPlayers() == null
                            || wrapper.getResponse().getPlayers().isEmpty()) {
                        return null;
                    }

                    SteamPlayer p = wrapper.getResponse().getPlayers().get(0);
                    PlayerDTO.SteamProfileResponse profile = new PlayerDTO.SteamProfileResponse();
                    profile.setSteamId(p.getSteamid());
                    profile.setDisplayName(p.getPersonaname());
                    profile.setAvatarUrl(p.getAvatarfull());
                    profile.setProfileUrl(p.getProfileurl());
                    profile.setLevel(level);
                    return profile;
                })
                .onErrorResume(ex -> {
                    log.error("Error llamando Steam API para steamId {}: {}", steamId, ex.getMessage());
                    return Mono.empty();
                });
    }

    @Data
    static class SteamSummaryWrapper {
        private SteamResponse response;
    }

    @Data
    static class SteamResponse {
        private List<SteamPlayer> players;
    }

    @Data
    static class SteamPlayer {
        private String steamid;
        private String personaname;
        private String avatarfull;
        private String profileurl;

        @JsonProperty("communityvisibilitystate")
        private Integer communityVisibilityState;
    }

    @Data
    static class SteamLevelWrapper {
        private SteamLevelResponse response;
    }

    @Data
    static class SteamLevelResponse {
        @JsonProperty("player_level")
        private Integer playerLevel;
    }
}
