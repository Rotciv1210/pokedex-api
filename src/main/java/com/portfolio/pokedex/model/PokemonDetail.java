package com.portfolio.pokedex.model;

import java.util.List;

public record PokemonDetail(
        int id,
        String name,
        int baseExperience,
        int height,
        int weight,
        String sprite,
        List<String> types,
        List<StatEntry> stats,
        List<String> abilities
) {
    public record StatEntry(String name, int baseStat) {}
}
