package utils;

import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;

import java.util.List;

public record TestResult<T>(boolean ok, T value, Seed seed, List<Result<?>> causes) implements Result<T> {}
