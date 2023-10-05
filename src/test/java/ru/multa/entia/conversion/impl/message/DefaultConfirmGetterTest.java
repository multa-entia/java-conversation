package ru.multa.entia.conversion.impl.message;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultConfirmGetterTest {

    @Test
    void shouldCheckGetting_ifArgsIsNull() {
        Result<Boolean> result = new DefaultConfirmGetter().apply(null);

        assertThat(result.ok()).isTrue();
        assertThat(result.value()).isNotNull();
        assertThat(result.seed()).isNull();
    }

    @Test
    void shouldCheckGetting_ifArgsDoesNotContainKey() {
        Result<Boolean> result = new DefaultConfirmGetter().apply(new Object[0]);

        assertThat(result.ok()).isTrue();
        assertThat(result.value()).isNotNull();
        assertThat(result.seed()).isNull();
    }

    @Test
    void shouldCheckGetting_ifArgsDoesNotContainValue() {
        Object[] args = {
                null,
                DefaultMessageFactory.Keys.CONFIRM
        };
        Result<Boolean> result = new DefaultConfirmGetter().apply(args);

        assertThat(result.ok()).isTrue();
        assertThat(result.value()).isNotNull();
        assertThat(result.seed()).isNull();
    }

    @Test
    void shouldCheckGetting_ifArgsContainsBadValue() {
        Object[] args = {
                null,
                DefaultMessageFactory.Keys.CONFIRM,
                Faker.str_().random()
        };
        Result<Boolean> result = new DefaultConfirmGetter().apply(args);

        assertThat(result.ok()).isTrue();
        assertThat(result.value()).isNotNull();
        assertThat(result.seed()).isNull();
    }

    @Test
    void shouldCheckGetting() {
        boolean expectedConfirm = true;
        Object[] args = {
                null,
                DefaultMessageFactory.Keys.CONFIRM,
                expectedConfirm
        };
        Result<Boolean> result = new DefaultConfirmGetter().apply(args);

        assertThat(result.ok()).isTrue();
        assertThat(result.value()).isEqualTo(expectedConfirm);
        assertThat(result.seed()).isNull();
    }
}