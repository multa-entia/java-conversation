package ru.multa.entia.conversion.impl.message;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.seed.Seed;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultMessageCheckerTest {

    @Test
    void shouldCheckChecking_ifInstanceNull() {
        Seed seed = new DefaultMessageChecker().check(null);

        assertThat(seed.code()).isEqualTo(DefaultMessageChecker.Code.IS_NULL.getValue());
        assertThat(seed.args()).isEmpty();
    }

    @Test
    void shouldCheckChecking() {
        Seed seed = new DefaultMessageChecker().check(Faker.str_().random());

        assertThat(seed).isNull();
    }
}