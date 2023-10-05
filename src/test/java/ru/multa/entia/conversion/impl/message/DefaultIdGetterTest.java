package ru.multa.entia.conversion.impl.message;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultIdGetterTest {

    @Test
    void shouldCheckGetting_ifArgsIsNull() {
        Result<UUID> result = new DefaultIdGetter().apply(null);

        assertThat(result.ok()).isTrue();
        assertThat(result.value()).isNotNull();
        assertThat(result.seed()).isNull();
    }

    @Test
    void shouldCheckGetting_ifArgsDoesNotContainKey() {
        Result<UUID> result = new DefaultIdGetter().apply(new Object[0]);

        assertThat(result.ok()).isTrue();
        assertThat(result.value()).isNotNull();
        assertThat(result.seed()).isNull();
    }

    @Test
    void shouldCheckGetting_ifArgsDoesNotContainValue() {
        Object[] args = {
                null,
                DefaultIdGetter.KEY
        };
        Result<UUID> result = new DefaultIdGetter().apply(args);

        assertThat(result.ok()).isTrue();
        assertThat(result.value()).isNotNull();
        assertThat(result.seed()).isNull();
    }

    @Test
    void shouldCheckGetting_ifArgsContainsBadValue() {
        Object[] args = {
                null,
                DefaultIdGetter.KEY,
                Faker.str_().random()
        };
        Result<UUID> result = new DefaultIdGetter().apply(args);

        assertThat(result.ok()).isTrue();
        assertThat(result.value()).isNotNull();
        assertThat(result.seed()).isNull();
    }

    @Test
    void shouldCheckGetting() {
        UUID expectedId = Faker.uuid_().random();
        Object[] args = {
                null,
                DefaultIdGetter.KEY,
                expectedId
        };
        Result<UUID> result = new DefaultIdGetter().apply(args);

        assertThat(result.ok()).isTrue();
        assertThat(result.value()).isEqualTo(expectedId);
        assertThat(result.seed()).isNull();
    }
}