package utils;

import ru.multa.entia.results.api.seed.Seed;

public record TestSeed(String code, Object[] args) implements Seed {}
