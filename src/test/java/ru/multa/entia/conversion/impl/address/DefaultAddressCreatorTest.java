package ru.multa.entia.conversion.impl.address;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.fakers.impl.Faker;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultAddressCreatorTest {
    @Test
    void shouldCheckCreation() {
        String expectedValue = Faker.str_().random();
        Address address = new DefaultAddressCreator().create(expectedValue);

        assertThat(address).isInstanceOf(DefaultAddress.class);
        assertThat(address.value()).isEqualTo(expectedValue);
    }
}