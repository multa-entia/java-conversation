package utils;

import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;

import java.util.Arrays;
import java.util.Objects;

public class ResultUtil {

    public static Seed seed(final String code, Object... args){
        return new TestSeed(code, args);
    }

    public static <T> Result<T> ok(T value){
        return new TestResult<>(true, value, null);
    }

    public static <T> Result<T> fail(Seed seed) {
        return new TestResult<>(false, null, seed);
    }

    public static <T> Result<T> fail(String code, Object... args){
        return new TestResult<>(false, null, seed(code, args));
    }

    public static boolean isEqual(Seed s0, Seed s1){
        if (s0 == null || s1 == null){
            return s0 == s1;
        }
        return s0.code().equals(s1.code()) && Arrays.equals(s0.args(), s1.args());
    }

    public static <T> boolean isEqual(Result<T> r0, Result<T> r1){
        return r0.ok() == r1.ok() && Objects.equals(r0.value(), r1.value()) && isEqual(r0.seed(), r1.seed());
    }
}
