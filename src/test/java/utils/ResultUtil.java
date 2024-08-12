package utils;

import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;

import java.util.List;

public class ResultUtil {

    public static Seed seed(final String code, Object... args){
        return new TestSeed(code, args);
    }

    public static <T> Result<T> ok(T value){
        return new TestResult<>(true, value, null, List.of());
    }

    public static <T> Result<T> fail(Seed seed) {
        return new TestResult<>(false, null, seed, List.of());
    }

    public static <T> Result<T> fail(String code, Object... args){
        return new TestResult<>(false, null, seed(code, args), List.of());
    }
}
