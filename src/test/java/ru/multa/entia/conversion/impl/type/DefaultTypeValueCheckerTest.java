package ru.multa.entia.conversion.impl.type;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.utils.Seeds;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTypeValueCheckerTest {

    @Test
    void shouldCheckChecking_ifInstanceNull() {
        Seed seed = new DefaultTypeValueChecker().check(null);

        assertThat(Seeds.comparator(seed).code(DefaultTypeValueChecker.Code.IS_NULL.getValue()).compare()).isTrue();
    }

    @Test
    void shouldCheckChecking_ifNotStr() {
        Seed seed = new DefaultTypeValueChecker().check(Faker.uuid_().random());

        assertThat(Seeds.comparator(seed).code(DefaultTypeValueChecker.Code.IS_NOT_STR.getValue()).compare()).isTrue();
    }

    @Test
    void shouldCheckSuccessChecking() {
        Seed seed = new DefaultTypeValueChecker().check(Faker.str_().random());

        assertThat(Seeds.comparator(seed).isNull().compare()).isTrue();
    }
}