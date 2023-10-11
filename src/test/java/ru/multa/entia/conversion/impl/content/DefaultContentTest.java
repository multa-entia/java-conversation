package ru.multa.entia.conversion.impl.content;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.type.Type;
import ru.multa.entia.fakers.impl.Faker;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultContentTest {

    @Test
    void shouldCheckTypeGetting_ifNull() {
        DefaultContent content = new DefaultContent(null, null);
        assertThat(content.type()).isNull();
    }

    @Test
    void shouldCheckTypeGetting() {
        TestType expectedType = new TestType(Faker.str_().random(5, 10));
        DefaultContent content = new DefaultContent(expectedType, null);

        assertThat(content.type()).isEqualTo(expectedType);
    }

    @Test
    void shouldCheckValueGetting_ifNull() {
        DefaultContent content = new DefaultContent(null, null);
        assertThat(content.value()).isNull();
    }

    @Test
    void shouldCheckValueGetting() {
        String expectedValue = Faker.str_().random(5, 10);
        DefaultContent content = new DefaultContent(null, expectedValue);

        assertThat(content.value()).isEqualTo(expectedValue);
    }

    // TODO: 11.10.2023 ???
    private record TestType(String value) implements Type {}
}