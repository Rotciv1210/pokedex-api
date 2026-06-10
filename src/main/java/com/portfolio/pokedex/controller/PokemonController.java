package com.portfolio.pokedex.controller;

import com.portfolio.pokedex.model.CompareResult;
import com.portfolio.pokedex.model.PokemonDetail;
import com.portfolio.pokedex.model.PokemonSummary;
import com.portfolio.pokedex.service.PokemonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/pokemon")
@Tag(name = "Pokemon", description = "Browse and search Pokemon")
public class PokemonController {

    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping
    @Operation(summary = "List Pokemon with pagination")
    public List<PokemonSummary> list(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        limit = Math.min(limit, 100);
        return pokemonService.list(offset, limit);
    }

    @GetMapping("/{name}")
    @Operation(summary = "Get detailed info for a Pokemon")
    public PokemonDetail getByName(@PathVariable String name) {
        return pokemonService.getByName(name);
    }

    @GetMapping("/random")
    @Operation(summary = "Get N random Pokemon (default 6)")
    public List<PokemonDetail> random(@RequestParam(defaultValue = "6") int count) {
        count = Math.min(count, 12);
        Random rng = new Random();
        List<PokemonDetail> result = new java.util.ArrayList<>();
        java.util.Set<Integer> used = new java.util.HashSet<>();
        while (result.size() < count) {
            int id = rng.nextInt(898) + 1;
            if (used.contains(id)) continue;
            used.add(id);
            try { result.add(pokemonService.getByName(String.valueOf(id))); }
            catch (Exception ignored) {}
        }
        return result;
    }

    @GetMapping("/compare")
    @Operation(summary = "Compare stats between two or more Pokemon")
    public ResponseEntity<CompareResult> compare(@RequestParam List<String> names) {
        if (names.size() < 2) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(pokemonService.compare(names));
    }
}
