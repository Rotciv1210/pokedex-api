package com.portfolio.pokedex.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Team {

    private final String id;
    private String name;
    private final List<PokemonSummary> members;

    public Team(String name) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.name = name;
        this.members = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<PokemonSummary> getMembers() { return members; }

    public boolean addMember(PokemonSummary pokemon) {
        if (members.size() >= 6) return false;
        if (members.stream().anyMatch(m -> m.name().equals(pokemon.name()))) return false;
        members.add(pokemon);
        return true;
    }

    public boolean removeMember(String pokemonName) {
        return members.removeIf(m -> m.name().equals(pokemonName));
    }
}
