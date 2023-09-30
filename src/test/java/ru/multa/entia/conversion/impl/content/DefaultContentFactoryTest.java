package ru.multa.entia.conversion.impl.content;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.value.Value;
import ru.multa.entia.conversion.impl.type.DefaultTypeFactory;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultContentFactoryTest {

    @Test
    void shouldCheckCreation_ifInstanceIsNull() {
        Result<Content> result = new DefaultContentFactory().create(null);

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(DefaultTypeFactory.CODE);
        assertThat(result.seed().args()).isEmpty();
    }

    @Test
    void shouldCheckCreation_ifInstanceIsNotChildOfValue() {
        Result<Content> result = new DefaultContentFactory().create(new BadParentTestValue());

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(DefaultContentFactory.CODE__BAD_PARENT);
        assertThat(result.seed().args()).isEmpty();
    }

    @Test
    void shouldCheckCreation_ifSerializationError() {
        Result<Content> result = new DefaultContentFactory().create(new BadAccessTestValue());

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(DefaultContentFactory.CODE__BAD_SERIALIZATION);
        assertThat(result.seed().args()).isEmpty();
    }

    @Test
    void shouldCheckCreation() {
        Integer initX = Faker.int_().between(0, 1_000);
        TestValue instance = new TestValue(initX);
        String expectedValue = String.format("{\"x\":%s}", initX);
        String expectedType = instance.getClass().getName();

        Result<Content> result = new DefaultContentFactory().create(instance);

        assertThat(result.ok()).isTrue();
        assertThat(result.value().value()).isEqualTo(expectedValue);
        assertThat(result.value().type().value()).isEqualTo(expectedType);
    }

    private static class BadParentTestValue {}

    private static class BadAccessTestValue implements Value{}

    public record TestValue(int x) implements Value {}
}