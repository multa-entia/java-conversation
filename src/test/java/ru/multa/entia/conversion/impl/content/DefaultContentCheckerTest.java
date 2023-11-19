package ru.multa.entia.conversion.impl.content;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.value.Value;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.seed.Seed;
import utils.ResultUtil;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: 18.11.2023 faked bool
class DefaultContentCheckerTest {

    @Test
    void shouldCheckChecking_ifInstanceIsNull() {
        Seed seed = new DefaultContentChecker().check(null);

        assertThat(ResultUtil.isEqual(seed, ResultUtil.seed(DefaultContentChecker.Code.IS_NULL.getValue()))).isTrue();
    }

    @Test
    void shouldCheckChecking_ifInstanceHasBadParent() {
        Seed seed = new DefaultContentChecker().check(new BadParentTestValue());

        assertThat(ResultUtil.isEqual(seed, ResultUtil.seed(DefaultContentChecker.Code.BAD_PARENT.getValue()))).isTrue();
    }

    @Test
    void shouldCheckChecking() {
        Seed seed = new DefaultContentChecker().check(new TestValue(Faker.int_().random()));

        assertThat(seed).isNull();
    }

    private static class BadParentTestValue {}
    public record TestValue(int x) implements Value {}
}