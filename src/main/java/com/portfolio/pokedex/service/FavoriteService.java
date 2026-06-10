package com.portfolio.pokedex.service;

import com.portfolio.pokedex.model.PokemonSummary;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FavoriteService {

    private final Map<String, PokemonSummary> favorites = new ConcurrentHashMap<>();
    private final PokemonService pokemonService;

    public FavoriteService(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    public PokemonSummary add(String name) {
        PokemonSummary summary = pokemonService.fetchSummary(name.toLowerCase());
        favorites.put(summary.name(), summary);
        return summary;
    }

    public boolean remove(String name) {
        return favorites.remove(name.toLowerCase()) != null;
    }

    public List<PokemonSummary> getAll() {
        return new ArrayList<>(favorites.values());
    }

    public boolean isFavorite(String name) {
        return favorites.containsKey(name.toLowerCase());
    }
}
