package ru.multa.entia.conversion.impl.getter;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.impl.message.DefaultMessageFactory;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;

import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: 29.10.2023 ME-16
class DefaultValueGetterTest {
    private static final UUID DEFAULT_VALUE = Faker.uuid_().random();
    private static final DefaultMessageFactory.Key KEY = DefaultMessageFactory.Key.ID;
    private static final Supplier<UUID> DEFAULT_SUPPLIER = () -> {return DEFAULT_VALUE;};

    @Test
    void shouldCheckGetting_ifArgsIsNull() {
        Result<UUID> result = new DefaultValueGetter<UUID, DefaultMessageFactory.Key>(KEY, DEFAULT_SUPPLIER).apply(null);

        assertThat(result.ok()).isTrue();
        assertThat(result.value()).isEqualTo(DEFAULT_VALUE);
        assertThat(result.seed()).isNull();
    }

    @Test
    void shouldCheckGetting_ifArgsDoesNotContainKey() {
        Result<UUID> result = new DefaultValueGetter<UUID, DefaultMessageFactory.Key>(KEY, DEFAULT_SUPPLIER).apply(new Object[0]);

        assertThat(result.ok()).isTrue();
        assertThat(result.value()).isEqualTo(DEFAULT_VALUE);
        assertThat(result.seed()).isNull();
    }

    @Test
    void shouldCheckGetting_ifArgsDoesNotContainValue() {
        Object[] args = {
                null,
                KEY
        };
        Result<UUID> result = new DefaultValueGetter<UUID, DefaultMessageFactory.Key>(KEY, DEFAULT_SUPPLIER).apply(args);

        assertThat(result.ok()).isTrue();
        assertThat(result.value()).isEqualTo(DEFAULT_VALUE);
        assertThat(result.seed()).isNull();
    }

    @Test
    void shouldCheckGetting_ifArgsContainsBadValue() {
        Object[] args = {
                null,
                KEY,
                Faker.str_().random()
        };
        Result<UUID> result = new DefaultValueGetter<UUID, DefaultMessageFactory.Key>(KEY, DEFAULT_SUPPLIER).apply(args);

        assertThat(result.ok()).isTrue();
        assertThat(result.value()).isEqualTo(DEFAULT_VALUE);
        assertThat(result.seed()).isNull();
    }

    @Test
    void shouldCheckGetting() {
        UUID expected = Faker.uuid_().random();
        Object[] args = {
                null,
                KEY,
                expected
        };
        Result<UUID> result = new DefaultValueGetter<UUID, DefaultMessageFactory.Key>(KEY, DEFAULT_SUPPLIER).apply(args);

        assertThat(result.ok()).isTrue();
        assertThat(result.value()).isEqualTo(expected);
        assertThat(result.seed()).isNull();
    }
}