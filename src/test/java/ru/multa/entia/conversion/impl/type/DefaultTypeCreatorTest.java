package ru.multa.entia.conversion.impl.type;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.type.Type;
import ru.multa.entia.fakers.impl.Faker;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTypeCreatorTest {

    @Test
    void shouldCheckCreation_ifValueNull() {
        Type type = new DefaultTypeCreator().create(null);

        assertThat(type.value()).isNull();
    }

    @Test
    void shouldCheckCreation() {
        String expectedValue = Faker.str_().random();
        Type type = new DefaultTypeCreator().create(expectedValue);

        assertThat(type.value()).isEqualTo(expectedValue);
    }
}