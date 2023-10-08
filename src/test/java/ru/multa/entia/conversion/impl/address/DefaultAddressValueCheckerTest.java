package ru.multa.entia.conversion.impl.address;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.seed.Seed;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultAddressValueCheckerTest {

    @Test
    void shouldCheckDefaultAddressValueChecker_ifInstanceIsNull() {
        Seed seed = new DefaultAddressValueChecker().apply(null);

        assertThat(seed.code()).isEqualTo(DefaultAddressValueChecker.Code.INSTANCE_IS_NULL.getValue());
        assertThat(seed.args()).isEmpty();
    }

    @Test
    void shouldCheckDefaultAddressValueChecker_ifInstanceIsNotString() {
        Seed seed = new DefaultAddressValueChecker().apply(Faker.int_().random());

        assertThat(seed.code()).isEqualTo(DefaultAddressValueChecker.Code.INSTANCE_IS_NOT_STR.getValue());
        assertThat(seed.args()).isEmpty();
    }

    @Test
    void shouldCheckDefaultAddressValueChecker_ifInstanceIsEmpty() {
        Seed seed = new DefaultAddressValueChecker().apply("");

        assertThat(seed.code()).isEqualTo(DefaultAddressValueChecker.Code.INSTANCE_IS_BLANK.getValue());
        assertThat(seed.args()).isEmpty();
    }

    @Test
    void shouldCheckDefaultAddressValueChecker_ifInstanceIsBlank() {
        Seed seed = new DefaultAddressValueChecker().apply("  ");

        assertThat(seed.code()).isEqualTo(DefaultAddressValueChecker.Code.INSTANCE_IS_BLANK.getValue());
        assertThat(seed.args()).isEmpty();
    }

    @Test
    void shouldCheckDefaultAddressValueChecker() {
        String expectedValue = Faker.str_().random(5, 10);
        Seed seed = new DefaultAddressValueChecker().apply(expectedValue);

        assertThat(seed).isNull();
    }
}