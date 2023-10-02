package ru.multa.entia.conversion.impl.confirmation;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultConfirmationTest {

    @Test
    void shouldCheckCodeGetting_ifNull() {
        DefaultConfirmation confirmation = new DefaultConfirmation(null, new Object[0]);

        assertThat(confirmation.code()).isNull();
    }

    @Test
    void shouldCheckCodeGetting() {
        String expectedCode = Faker.str_().random(5, 10);
        DefaultConfirmation confirmation = new DefaultConfirmation(expectedCode, new Object[0]);

        assertThat(confirmation.code()).isEqualTo(expectedCode);
    }

    @Test
    void shouldCheckArgsGetting_ifNull() {
        DefaultConfirmation confirmation = new DefaultConfirmation(null, null);

        assertThat(confirmation.args()).isNull();
    }

    @Test
    void shouldCheckArgsGetting() {
        Object[] expectedArgs = {
                Faker.str_().random(),
                Faker.str_().random(),
                Faker.str_().random()
        };
        DefaultConfirmation confirmation = new DefaultConfirmation(null, expectedArgs);

        assertThat(Arrays.equals(confirmation.args(), expectedArgs)).isTrue();
    }
}