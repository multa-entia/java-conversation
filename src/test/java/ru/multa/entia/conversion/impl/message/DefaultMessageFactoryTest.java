package ru.multa.entia.conversion.impl.message;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

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
}