package ru.multa.entia.conversion.impl.message;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.fakers.impl.Faker;
import utils.TestAddress;
import utils.TestContent;
import utils.TestType;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultMessageTest {

    @Test
    void shouldCheckGetting_ifNullOrFalse() {
        DefaultMessage message
                = new DefaultMessage(null, null, null, null, false, null);

        assertThat(message.id()).isNull();
        assertThat(message.conversation()).isNull();
        assertThat(message.from()).isNull();
        assertThat(message.to()).isNull();
        assertThat(message.confirm()).isFalse();
        assertThat(message.content()).isNull();
    }

    @Test
    void shouldCheckIdGetting() {
        UUID expectedId = Faker.uuid_().random();
        DefaultMessage message
                = new DefaultMessage(expectedId, null, null, null, false, null);

        assertThat(message.id()).isEqualTo(expectedId);
    }

    @Test
    void shouldCheckConversationGetting() {
        UUID expectedConversation = Faker.uuid_().random();
        DefaultMessage message
                = new DefaultMessage(null, expectedConversation, null, null, false, null);

        assertThat(message.conversation()).isEqualTo(expectedConversation);
    }

    @Test
    void shouldCheckFromGetting() {
        Address expectedFrom = new TestAddress(Faker.str_().random());
        DefaultMessage message
                = new DefaultMessage(null, null, expectedFrom, null, false, null);

        assertThat(message.from()).isEqualTo(expectedFrom);
    }

    @Test
    void shouldCheckToGetting() {
        Address expectedTo = new TestAddress(Faker.str_().random());
        DefaultMessage message
                = new DefaultMessage(null, null, null, expectedTo, false, null);

        assertThat(message.to()).isEqualTo(expectedTo);
    }

    @Test
    void shouldCheckConfirmGetting() {
        boolean expectedConfirm = true;
        DefaultMessage message
                = new DefaultMessage(null, null, null, null, expectedConfirm, null);

        assertThat(message.confirm()).isEqualTo(expectedConfirm);
    }

    @Test
    void shouldCheckContentGetting() {
        Content expectedContent = new TestContent(new TestType(Faker.str_().random()), Faker.str_().random());
        DefaultMessage message
                = new DefaultMessage(null, null, null, null, false, expectedContent);

        assertThat(message.content()).isEqualTo(expectedContent);
    }
}