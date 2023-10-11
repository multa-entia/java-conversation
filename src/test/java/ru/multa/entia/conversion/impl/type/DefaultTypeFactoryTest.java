package ru.multa.entia.conversion.impl.type;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.conversion.api.type.Type;
import ru.multa.entia.conversion.api.type.TypeCreator;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTypeFactoryTest {
    @Test
    void shouldCheckCreation_ifFailChecking() {
        String expectedCode = Faker.str_().random();
        TestChecker checker = instance -> {
            Seed seed = Mockito.mock(Seed.class);
            Mockito.when(seed.code()).thenReturn(expectedCode);
            Mockito.when(seed.args()).thenReturn(new Object[0]);

            return seed;
        };

        DefaultTypeFactory factory = new DefaultTypeFactory(checker, null);

        Result<Type> result = factory.create(null);

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(expectedCode);
        assertThat(result.seed().args()).isEmpty();
    }

    @Test
    void shouldCheckCreation() {
        String expectedValue = Faker.str_().random(5, 10);
        TestChecker checker = instance -> {return null;};
        TypeCreator creator = instance -> {
            Type type = Mockito.mock(Type.class);
            Mockito.when(type.value()).thenReturn(instance);
            return type;
        };

        Result<Type> result = new DefaultTypeFactory(checker, creator).create(expectedValue);

        assertThat(result.ok()).isTrue();
        assertThat(result.value().value()).isEqualTo(expectedValue);
        assertThat(result.seed()).isNull();
    }

    private interface TestChecker extends Function<Object, Seed>{}
}