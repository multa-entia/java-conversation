package ru.multa.entia.conversion.impl.confirmation;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.confirmation.Confirmation;
import ru.multa.entia.fakers.impl.Faker;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultConfirmationCreatorTest {

    @Test
    void shouldCheckCreation() {
        UUID expectedId = Faker.uuid_().random();
        UUID expectedConversation = Faker.uuid_().random();
        String expectedFromValue = Faker.str_().random();
        String expectedToValue = Faker.str_().random();
        String expectedCode = Faker.str_().random();
        Object[] expectedArgs = {
                Faker.str_().random(),
                Faker.int_().random()
        };

        Confirmation confirmation = new DefaultConfirmationCreator().create(
                expectedId,
                expectedConversation,
                createAddress(expectedFromValue),
                createAddress(expectedToValue),
                expectedCode,
                expectedArgs
        );

        assertThat(confirmation).isInstanceOf(DefaultConfirmation.class);
        assertThat(confirmation.id()).isEqualTo(expectedId);
        assertThat(confirmation.conversation()).isEqualTo(expectedConversation);
        assertThat(confirmation.from().value()).isEqualTo(expectedFromValue);
        assertThat(confirmation.to().value()).isEqualTo(expectedToValue);
        assertThat(confirmation.code()).isEqualTo(expectedCode);
        assertThat(Arrays.equals(confirmation.args(), expectedArgs)).isTrue();
    }

    // TODO: 11.10.2023 use lambda
    private Address createAddress(final String value){
        Address address = Mockito.mock(Address.class);
        Mockito
                .when(address.value())
                .thenReturn(value);

        return address;
    }
}