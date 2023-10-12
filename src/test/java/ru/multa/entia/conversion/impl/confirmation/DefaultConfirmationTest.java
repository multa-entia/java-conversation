package ru.multa.entia.conversion.impl.confirmation;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.fakers.impl.Faker;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultConfirmationTest {

    @Test
    void shouldCheckIdGetting_ifNull() {
        DefaultConfirmation confirmation = new DefaultConfirmation(
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertThat(confirmation.id()).isNull();
    }

    @Test
    void shouldCheckIdGetting() {
        UUID expectedId = Faker.uuid_().random();
        DefaultConfirmation confirmation = new DefaultConfirmation(
                expectedId,
                null,
                null,
                null,
                null,
                null
        );

        assertThat(confirmation.id()).isEqualTo(expectedId);
    }

    @Test
    void shouldCheckConversationGetting_ifNull() {
        DefaultConfirmation confirmation = new DefaultConfirmation(
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertThat(confirmation.conversation()).isNull();
    }

    @Test
    void shouldCheckConversationGetting() {
        UUID expectedConversation = Faker.uuid_().random();
        DefaultConfirmation confirmation = new DefaultConfirmation(
                null,
                expectedConversation,
                null,
                null,
                null,
                null
        );

        assertThat(confirmation.conversation()).isEqualTo(expectedConversation);
    }

    @Test
    void shouldCheckFromGetting_ifNull() {
        DefaultConfirmation confirmation = new DefaultConfirmation(
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertThat(confirmation.from()).isNull();
    }

    @Test
    void shouldCheckFromGetting() {
        TestAddress expectedFrom = new TestAddress(Faker.str_().random());
        DefaultConfirmation confirmation = new DefaultConfirmation(
                null,
                null,
                expectedFrom,
                null,
                null,
                null
        );

        assertThat(confirmation.from()).isEqualTo(expectedFrom);
    }

    @Test
    void shouldCheckToGetting_ifNull() {
        DefaultConfirmation confirmation = new DefaultConfirmation(
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertThat(confirmation.to()).isNull();
    }

    @Test
    void shouldCheckToGetting() {
        TestAddress expectedTo = new TestAddress(Faker.str_().random());
        DefaultConfirmation confirmation = new DefaultConfirmation(
                null,
                null,
                null,
                expectedTo,
                null,
                null
        );

        assertThat(confirmation.to()).isEqualTo(expectedTo);
    }

    @Test
    void shouldCheckCodeGetting_ifNull() {
        DefaultConfirmation confirmation = new DefaultConfirmation(
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertThat(confirmation.code()).isNull();
    }

    @Test
    void shouldCheckCodeGetting() {
        String expectedCode = Faker.str_().random();
        DefaultConfirmation confirmation = new DefaultConfirmation(
                null,
                null,
                null,
                null,
                expectedCode,
                null
        );

        assertThat(confirmation.code()).isEqualTo(expectedCode);
    }

    @Test
    void shouldCheckArgsGetting_ifNull() {
        DefaultConfirmation confirmation = new DefaultConfirmation(
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertThat(confirmation.args()).isNull();
    }

    @Test
    void shouldCheckArgsGetting() {
        Object[] expectedArgs = {
                Faker.str_().random(),
                Faker.str_().random(),
                Faker.str_().random()
        };
        DefaultConfirmation confirmation = new DefaultConfirmation(
                null,
                null,
                null,
                null,
                null,
                expectedArgs
        );

        assertThat(Arrays.equals(confirmation.args(), expectedArgs)).isTrue();
    }

    private record TestAddress(String value) implements Address {}
}