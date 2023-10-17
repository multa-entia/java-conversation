package ru.multa.entia.conversion.impl.type;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.conversion.api.type.Type;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import utils.TestSeed;
import utils.TestType;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTypeFactoryTest {
    @Test
    void shouldCheckCreation_ifFailChecking() {
        String expectedCode = Faker.str_().random();
        Checker<Object> checker = instance -> {
            return new TestSeed(expectedCode, new Object[0]);
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
        Checker<Object> checker = instance -> {return null;};

        Result<Type> result = new DefaultTypeFactory(checker, TestType::new).create(expectedValue);

        assertThat(result.ok()).isTrue();
        assertThat(result.value().value()).isEqualTo(expectedValue);
        assertThat(result.seed()).isNull();
    }
}