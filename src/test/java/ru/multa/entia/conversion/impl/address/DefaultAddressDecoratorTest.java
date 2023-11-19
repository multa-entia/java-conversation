package ru.multa.entia.conversion.impl.address;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.fakers.impl.Faker;
import utils.TestAddress;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: 18.11.2023 faked bool
class DefaultAddressDecoratorTest {

    @Test
    void shouldCheckDecoration() {
        String expectedValue = Faker.str_().random();
        Address initAddress = new TestAddress(expectedValue);
        Address address = new DefaultAddressDecorator(TestAddress::new).decorate(initAddress);

        assertThat(address.value()).isEqualTo(expectedValue);
    }

    @Test
    void shouldCheckCreation_withTemplate() {
        String template = "{{ %s }}";
        String initValue = Faker.str_().random();

        Address address = new DefaultAddressDecorator(template).decorate(new TestAddress(initValue));

        assertThat(address.value()).isEqualTo(String.format(template, initValue));
    }
}