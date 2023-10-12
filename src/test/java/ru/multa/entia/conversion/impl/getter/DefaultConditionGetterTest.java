package ru.multa.entia.conversion.impl.getter;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.impl.message.DefaultMessageFactory;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;
import utils.TestAddress;
import utils.TestSeed;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultConditionGetterTest {
    private static final String CODE = Faker.str_().random(5, 10);
    private static final DefaultMessageFactory.Key KEY = DefaultMessageFactory.Key.ID;
    private static final Function<Object, Seed> CONDITION = address -> {
        return address != null ? null : new TestSeed(CODE, new Object[0]);
    };

    @Test
    void shouldCheckGetting_ifArgsIsNull() {
        Result<Address> result = new DefaultConditionGetter<Address, DefaultMessageFactory.Key>(KEY, CONDITION).apply(null);

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(CODE);
    }

    @Test
    void shouldCheckGetting_ifArgsDoesNotContainKey() {
        Result<Address> result = new DefaultConditionGetter<Address, DefaultMessageFactory.Key>(KEY, CONDITION).apply(new Object[0]);

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(CODE);
    }

    @Test
    void shouldCheckGetting_ifArgsDoesNotContainValue() {
        Object[] args = {
                null,
                KEY
        };
        Result<Address> result = new DefaultConditionGetter<Address, DefaultMessageFactory.Key>(KEY, CONDITION).apply(args);

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(CODE);
    }

    @Test
    void shouldCheckGetting_ifBadValue() {
        Object[] args = {
                null,
                KEY,
                null
        };

        String expectedCode = Faker.str_().random();
        Function<Object, Seed> condition = address -> {
            return new TestSeed(expectedCode, new Object[0]);
        };

        Result<Address> result = new DefaultConditionGetter<Address, DefaultMessageFactory.Key>(KEY, condition).apply(args);

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(expectedCode);
    }

    @Test
    void shouldCheckGetting() {
        String expectedValue = Faker.str_().random();
        Object[] args = {
                null,
                KEY,
                new TestAddress(expectedValue)
        };
        Function<Object, Seed> condition = address -> {
            return null;
        };

        Result<Address> result = new DefaultConditionGetter<Address, DefaultMessageFactory.Key>(KEY, condition).apply(args);

        assertThat(result.ok()).isTrue();
        assertThat(result.value().value()).isEqualTo(expectedValue);
        assertThat(result.seed()).isNull();
    }
}