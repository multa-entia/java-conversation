package ru.multa.entia.conversion.impl.type;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.type.Type;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTypeFactoryTest {

    @Test
    void shouldCheckCreation_ifInstanceNull() {
        String expectedCode = "conversation.factory.type.instance-is-null";
        Result<Type> result = new DefaultTypeFactory().create(null);

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(expectedCode);
        assertThat(result.seed().args()).isEmpty();
    }

    @Test
    void shouldCheckCreation() {
        String instance = Faker.str_().random(5, 10);
        String expectedValue = instance.getClass().getName();

        Result<Type> result = new DefaultTypeFactory().create(instance);

        assertThat(result.ok()).isTrue();
        assertThat(result.value().value()).isEqualTo(expectedValue);
        assertThat(result.seed()).isNull();
    }
}