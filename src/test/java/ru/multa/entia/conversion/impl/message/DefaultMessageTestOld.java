package ru.multa.entia.conversion.impl.message;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.type.Type;
import ru.multa.entia.fakers.impl.Faker;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: 01.10.2023 rename
class DefaultMessageTestOld {

    @Test
    void shouldCheckIdGetting_ifNull() {
        DefaultMessageOld message = new DefaultMessageOld(null, false, null);

        assertThat(message.id()).isNull();
    }

    @Test
    void shouldCheckIdGetting() {
        UUID expectedId = Faker.uuid_().random();
        DefaultMessageOld message = new DefaultMessageOld(expectedId, false, null);

        assertThat(message.id()).isEqualTo(expectedId);
    }

    @Test
    void shouldCheckIsRequestGetting_ifFalse() {
        DefaultMessageOld message = new DefaultMessageOld(null, false, null);

        assertThat(message.isRequest()).isFalse();
    }

    @Test
    void shouldCheckIsRequestGetting() {
        DefaultMessageOld message = new DefaultMessageOld(null, true, null);

        assertThat(message.isRequest()).isTrue();
    }

    @Test
    void shouldCheckContentGetting_iNull() {
        DefaultMessageOld message = new DefaultMessageOld(null, false, null);

        assertThat(message.content()).isNull();
    }

    @Test
    void shouldCheckContentGetting() {
        TestContent expectedContent = new TestContent(Faker.str_().random());
        DefaultMessageOld message = new DefaultMessageOld(null, false, expectedContent);

        assertThat(message.content()).isEqualTo(expectedContent);
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    private static class TestContent implements Content{
        private final String value;

        @Override
        public Type type() {return null;}
        @Override
        public String value() {return null;}
    }
}