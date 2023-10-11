package ru.multa.entia.conversion.impl.type;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.seed.Seed;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTypeValueCheckerTest {

    @Test
    void shouldCheckChecking_ifInstanceNull() {
        Seed seed = new DefaultTypeValueChecker().apply(null);

        assertThat(seed.code()).isEqualTo(DefaultTypeValueChecker.Code.IS_NULL.getValue());
        assertThat(seed.args()).isEmpty();
    }

    @Test
    void shouldCheckChecking_ifNotStr() {
        Seed seed = new DefaultTypeValueChecker().apply(Faker.uuid_().random());

        assertThat(seed.code()).isEqualTo(DefaultTypeValueChecker.Code.IS_NOT_STR.getValue());
        assertThat(seed.args()).isEmpty();
    }

    @Test
    void shouldCheckSuccessChecking() {
        Seed seed = new DefaultTypeValueChecker().apply(Faker.str_().random());

        assertThat(seed).isNull();
    }
}