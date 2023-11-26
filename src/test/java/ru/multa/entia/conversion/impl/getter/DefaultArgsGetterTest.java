package ru.multa.entia.conversion.impl.getter;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.impl.confirmation.DefaultConfirmationFactory;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.utils.Results;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultArgsGetterTest {
    private static final DefaultConfirmationFactory.Key KEY = DefaultConfirmationFactory.Key.ARGS;

    @Test
    void shouldCheckGetting_ifArgsIsNull() {
        Result<Object[]> result = new DefaultArgsGetter<DefaultConfirmationFactory.Key>(KEY).apply(null);

        assertThat(Results.comparator(result)
                .isSuccess()
                .seedsComparator()
                .isNull()
                .back()
                .compare()).isTrue();
        assertThat(result.value()).isEmpty();
    }

    @Test
    void shouldCheckGetting_ifArgsDoesNotContainKey() {
        Result<Object[]> result = new DefaultArgsGetter<DefaultConfirmationFactory.Key>(KEY).apply(new Object[0]);

        assertThat(Results.comparator(result)
                .isSuccess()
                .seedsComparator()
                .isNull()
                .back()
                .compare()).isTrue();
        assertThat(result.value()).isEmpty();
    }

    @Test
    void shouldCheckGetting_ifArgsDoesNotContainValues() {
        Object[] args = {
                null,
                KEY
        };
        Result<Object[]> result = new DefaultArgsGetter<DefaultConfirmationFactory.Key>(KEY).apply(args);

        assertThat(Results.comparator(result)
                .isSuccess()
                .seedsComparator()
                .isNull()
                .back()
                .compare()).isTrue();
        assertThat(result.value()).isEmpty();
    }

    @Test
    void shouldCheckGetting() {
        String arg0 = Faker.str_().random();
        Integer arg1 = Faker.int_().random();
        Long arg2 = Faker.long_().random();
        UUID arg3 = Faker.uuid_().random();
        Object[] expectedArgs = {arg0, arg1, arg2, arg3};

        Object[] args = {null, KEY, arg0, arg1, arg2, arg3};
        Result<Object[]> result = new DefaultArgsGetter<DefaultConfirmationFactory.Key>(KEY).apply(args);

        assertThat(Results.comparator(result)
                .isSuccess()
                .seedsComparator()
                .isNull()
                .back()
                .compare()).isTrue();
        assertThat(Arrays.equals(result.value(), expectedArgs)).isTrue();
    }
}