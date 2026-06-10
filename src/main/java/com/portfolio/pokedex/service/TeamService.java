package com.portfolio.pokedex.service;

import com.portfolio.pokedex.model.PokemonSummary;
import com.portfolio.pokedex.model.Team;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TeamService {

    private final Map<String, Team> teams = new ConcurrentHashMap<>();
    private final PokemonService pokemonService;

    public TeamService(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    public Team create(String name) {
        Team team = new Team(name);
        teams.put(team.getId(), team);
        return team;
    }

    public List<Team> getAll() {
        return new ArrayList<>(teams.values());
    }

    public Optional<Team> getById(String id) {
        return Optional.ofNullable(teams.get(id));
    }

    public boolean delete(String id) {
        return teams.remove(id) != null;
    }

    public Team addPokemon(String teamId, String pokemonName) {
        Team team = teams.get(teamId);
        if (team == null) throw new NoSuchElementException("Team not found: " + teamId);

        PokemonSummary summary = pokemonService.fetchSummary(pokemonName.toLowerCase());
        if (!team.addMember(summary)) {
            throw new IllegalStateException("Team is full or Pokemon already in team");
        }
        return team;
    }

    public Team removePokemon(String teamId, String pokemonName) {
        Team team = teams.get(teamId);
        if (team == null) throw new NoSuchElementException("Team not found: " + teamId);
        team.removeMember(pokemonName.toLowerCase());
        return team;
    }
}
