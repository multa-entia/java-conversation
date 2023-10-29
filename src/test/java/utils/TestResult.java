package utils;

import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;

public record TestResult<T>(boolean ok, T value, Seed seed) implements Result<T> {}
