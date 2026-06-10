package com.portfolio.pokedex.service;

import com.portfolio.pokedex.model.CompareResult;
import com.portfolio.pokedex.model.PokemonDetail;
import com.portfolio.pokedex.model.PokemonSummary;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PokemonService {

    private final WebClient client;

    public PokemonService(WebClient pokeApiClient) {
        this.client = pokeApiClient;
    }

    @Cacheable(value = "pokemon-list", key = "#offset + '-' + #limit")
    public List<PokemonSummary> list(int offset, int limit) {
        var response = client.get()
                .uri("/pokemon?offset={o}&limit={l}", offset, limit)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        @SuppressWarnings("unchecked")
        List<Map<String, String>> results = (List<Map<String, String>>) response.get("results");

        return results.stream()
                .map(r -> fetchSummary(r.get("name")))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "pokemon", key = "#name.toLowerCase()")
    public PokemonDetail getByName(String name) {
        @SuppressWarnings("unchecked")
        Map<String, Object> raw = client.get()
                .uri("/pokemon/{name}", name.toLowerCase())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return mapToDetail(raw);
    }

    public PokemonSummary fetchSummary(String name) {
        @SuppressWarnings("unchecked")
        Map<String, Object> raw = client.get()
                .uri("/pokemon/{name}", name.toLowerCase())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        int id = ((Number) raw.get("id")).intValue();
        @SuppressWarnings("unchecked")
        Map<String, Object> sprites = (Map<String, Object>) raw.get("sprites");
        String sprite = (String) sprites.get("front_default");

        return new PokemonSummary(id, name, sprite);
    }

    public CompareResult compare(List<String> names) {
        List<PokemonDetail> details = names.stream()
                .map(this::getByName)
                .collect(Collectors.toList());

        Map<String, Map<String, Integer>> statsMap = new LinkedHashMap<>();
        for (PokemonDetail d : details) {
            for (PokemonDetail.StatEntry s : d.stats()) {
                statsMap.computeIfAbsent(s.name(), k -> new LinkedHashMap<>())
                        .put(d.name(), s.baseStat());
            }
        }

        Map<String, String> winners = new LinkedHashMap<>();
        statsMap.forEach((stat, values) -> {
            String winner = values.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("tie");
            winners.put(stat, winner);
        });

        return new CompareResult(names, statsMap, winners);
    }

    @SuppressWarnings("unchecked")
    private PokemonDetail mapToDetail(Map<String, Object> raw) {
        int id = ((Number) raw.get("id")).intValue();
        String name = (String) raw.get("name");
        int baseExp = raw.get("base_experience") != null ? ((Number) raw.get("base_experience")).intValue() : 0;
        int height = ((Number) raw.get("height")).intValue();
        int weight = ((Number) raw.get("weight")).intValue();

        Map<String, Object> sprites = (Map<String, Object>) raw.get("sprites");
        String sprite = (String) sprites.get("front_default");

        List<String> types = ((List<Map<String, Object>>) raw.get("types")).stream()
                .map(t -> (String) ((Map<String, Object>) t.get("type")).get("name"))
                .collect(Collectors.toList());

        List<PokemonDetail.StatEntry> stats = ((List<Map<String, Object>>) raw.get("stats")).stream()
                .map(s -> new PokemonDetail.StatEntry(
                        (String) ((Map<String, Object>) s.get("stat")).get("name"),
                        ((Number) s.get("base_stat")).intValue()))
                .collect(Collectors.toList());

        List<String> abilities = ((List<Map<String, Object>>) raw.get("abilities")).stream()
                .map(a -> (String) ((Map<String, Object>) a.get("ability")).get("name"))
                .collect(Collectors.toList());

        return new PokemonDetail(id, name, baseExp, height, weight, sprite, types, stats, abilities);
    }
}
