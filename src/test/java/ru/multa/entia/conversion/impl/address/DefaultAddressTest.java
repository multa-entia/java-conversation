package ru.multa.entia.conversion.impl.address;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: 18.11.2023 faked bool
class DefaultAddressTest {

    @Test
    void shouldCheckCreation_ifValueNull() {
        DefaultAddress address = new DefaultAddress(null);

        assertThat(address.value()).isNull();
    }

    @Test
    void shouldCheckCreation() {
        String expectedValue = Faker.str_().random(5, 10);
        DefaultAddress address = new DefaultAddress(expectedValue);

        assertThat(address.value()).isEqualTo(expectedValue);
    }
}