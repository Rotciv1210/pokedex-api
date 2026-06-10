package com.portfolio.pokedex.controller;

import com.portfolio.pokedex.model.Team;
import com.portfolio.pokedex.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
@Tag(name = "Teams", description = "Build and manage Pokemon teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    @Operation(summary = "Get all teams")
    public List<Team> getAll() {
        return teamService.getAll();
    }

    @PostMapping
    @Operation(summary = "Create a new team")
    public ResponseEntity<Team> create(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.create(name));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a team by ID")
    public ResponseEntity<Team> getById(@PathVariable String id) {
        return teamService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a team")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {
        boolean deleted = teamService.delete(id);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("message", "Team deleted"));
    }

    @PostMapping("/{id}/pokemon/{name}")
    @Operation(summary = "Add a Pokemon to a team (max 6)")
    public ResponseEntity<Team> addPokemon(@PathVariable String id, @PathVariable String name) {
        Team team = teamService.addPokemon(id, name);
        return ResponseEntity.ok(team);
    }

    @DeleteMapping("/{id}/pokemon/{name}")
    @Operation(summary = "Remove a Pokemon from a team")
    public ResponseEntity<Team> removePokemon(@PathVariable String id, @PathVariable String name) {
        Team team = teamService.removePokemon(id, name);
        return ResponseEntity.ok(team);
    }
}
