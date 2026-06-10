package com.portfolio.pokedex.model;

import java.util.List;
import java.util.Map;

public record CompareResult(
        List<String> pokemon,
        Map<String, Map<String, Integer>> stats,
        Map<String, String> winner
) {}
