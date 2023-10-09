package ru.multa.entia.conversion.impl.address;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.fakers.impl.Faker;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultAddressDecoratorTest {

    @Test
    void shouldCheckDecoration() {
        String expectedValue = Faker.str_().random();
        Address initAddress = createAddress(expectedValue);
        Address address = new DefaultAddressDecorator(this::createAddress).decorate(initAddress);

        assertThat(address).isNotEqualTo(initAddress);
        assertThat(address.value()).isEqualTo(expectedValue);
    }

    @Test
    void shouldCheckCreation_withTemplate() {
        String template = "{{ %s }}";
        String initValue = Faker.str_().random();

        Address address = new DefaultAddressDecorator(template).decorate(createAddress(initValue));

        assertThat(address.value()).isEqualTo(String.format(template, initValue));
    }

    private Address createAddress(final String value){
        Address address = Mockito.mock(Address.class);
        Mockito
                .when(address.value())
                .thenReturn(value);

        return address;
    }
}