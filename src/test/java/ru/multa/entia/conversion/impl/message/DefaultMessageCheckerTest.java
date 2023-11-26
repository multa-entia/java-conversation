package ru.multa.entia.conversion.impl.message;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.utils.Seeds;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultMessageCheckerTest {

    @Test
    void shouldCheckChecking_ifInstanceNull() {
        Seed seed = new DefaultMessageChecker().check(null);

        assertThat(Seeds.comparator(seed).code(DefaultMessageChecker.Code.IS_NULL.getValue()).compare()).isTrue();
    }

    @Test
    void shouldCheckChecking() {
        Seed seed = new DefaultMessageChecker().check(Faker.str_().random());

        assertThat(Seeds.comparator(seed).isNull().compare()).isTrue();
    }
}