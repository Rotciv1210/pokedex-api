package com.portfolio.pokedex.controller;

import com.portfolio.pokedex.model.PokemonSummary;
import com.portfolio.pokedex.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorites", description = "Manage your favorite Pokemon")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    @Operation(summary = "Get all favorite Pokemon")
    public List<PokemonSummary> getAll() {
        return favoriteService.getAll();
    }

    @PostMapping("/{name}")
    @Operation(summary = "Add a Pokemon to favorites")
    public PokemonSummary add(@PathVariable String name) {
        return favoriteService.add(name);
    }

    @DeleteMapping("/{name}")
    @Operation(summary = "Remove a Pokemon from favorites")
    public ResponseEntity<Map<String, String>> remove(@PathVariable String name) {
        boolean removed = favoriteService.remove(name);
        if (!removed) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("message", name + " removed from favorites"));
    }

    @GetMapping("/{name}/check")
    @Operation(summary = "Check if a Pokemon is in favorites")
    public Map<String, Object> check(@PathVariable String name) {
        return Map.of("name", name, "favorite", favoriteService.isFavorite(name));
    }
}
