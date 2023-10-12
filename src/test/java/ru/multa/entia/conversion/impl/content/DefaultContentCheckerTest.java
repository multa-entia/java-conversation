package ru.multa.entia.conversion.impl.content;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.value.Value;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.seed.Seed;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultContentCheckerTest {

    @Test
    void shouldCheckChecking_ifInstanceIsNull() {
        Seed seed = new DefaultContentChecker().check(null);

        assertThat(seed.code()).isEqualTo(DefaultContentChecker.Code.IS_NULL.getValue());
        assertThat(seed.args()).isEmpty();
    }

    @Test
    void shouldCheckChecking_ifInstanceHasBadParent() {
        Seed seed = new DefaultContentChecker().check(new BadParentTestValue());

        assertThat(seed.code()).isEqualTo(DefaultContentChecker.Code.BAD_PARENT.getValue());
        assertThat(seed.args()).isEmpty();
    }

    @Test
    void shouldCheckChecking() {
        Seed seed = new DefaultContentChecker().check(new TestValue(Faker.int_().random()));

        assertThat(seed).isNull();
    }

    private static class BadParentTestValue {}
    public record TestValue(int x) implements Value {}
}