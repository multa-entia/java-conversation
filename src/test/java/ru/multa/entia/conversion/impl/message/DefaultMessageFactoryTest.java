package ru.multa.entia.conversion.impl.message;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultMessageFactoryTest {

    @Test
    void shouldCheckIdGetting_ifArgsNull() {
        UUID result = new DefaultMessageFactory.IdGetter().apply(null);

        assertThat(result).isNotNull();
    }

    @Test
    void shouldCheckIdGetting_ifArgsDoesNotContainKey() {
        UUID result = new DefaultMessageFactory.IdGetter().apply(new Object[0]);

        assertThat(result).isNotNull();
    }

    @Test
    void shouldCheckIdGetting_ifArgsDoesNotContainValue() {
        Object[] args = new Object[]{DefaultMessageFactory.KEY__ID};
        UUID result = new DefaultMessageFactory.IdGetter().apply(args);

        assertThat(result).isNotNull();
    }

    @Test
    void shouldCheckIdGetting() {
        UUID expectedId = UUID.randomUUID();
        Object[] args = new Object[]{DefaultMessageFactory.KEY__ID, expectedId};
        UUID result = new DefaultMessageFactory.IdGetter().apply(args);

        assertThat(result).isEqualTo(expectedId);
    }

    @Test
    void shouldCheckIsRequestGetting_ifArgsNull() {
        Boolean result = new DefaultMessageFactory.IsRequestGetter().apply(null);

        assertThat(result).isEqualTo(DefaultMessageFactory.DEFAULT_IS_REQUEST);
    }

    @Test
    void shouldCheckIsRequestGetting_ifArgsDoesNotContainKey() {
        Boolean result = new DefaultMessageFactory.IsRequestGetter().apply(new Object[0]);

        assertThat(result).isEqualTo(DefaultMessageFactory.DEFAULT_IS_REQUEST);
    }

    @Test
    void shouldCheckIsRequestGetting_ifArgsDoesNotContainValue() {
        Object[] args = {DefaultMessageFactory.KEY__IS_REQUEST};
        Boolean result = new DefaultMessageFactory.IsRequestGetter().apply(args);

        assertThat(result).isEqualTo(DefaultMessageFactory.DEFAULT_IS_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldCheckIsRequestGetting(boolean expected) {
        Object[] args = new Object[]{DefaultMessageFactory.KEY__IS_REQUEST, expected};
        Boolean result = new DefaultMessageFactory.IsRequestGetter().apply(args);

        assertThat(result).isEqualTo(expected);
    }
}