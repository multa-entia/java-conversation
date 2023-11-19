package ru.multa.entia.conversion.impl.type;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: 18.11.2023 faked bool
class DefaultTypeTest {

    @Test
    void shouldCheckGetting_ifNull() {
        DefaultType type = new DefaultType(null);
        assertThat(type.value()).isNull();
    }

    @Test
    void shouldCheckGetting() {
        String expectedValue = Faker.str_().random(5, 10);
        DefaultType type = new DefaultType(expectedValue);

        assertThat(type.value()).isEqualTo(expectedValue);
    }
}