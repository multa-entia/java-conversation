package ru.multa.entia.conversion.impl.message;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.type.Type;
import ru.multa.entia.fakers.impl.Faker;

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
        Address expectedFrom = createAddress(Faker.str_().random());
        DefaultMessage message
                = new DefaultMessage(null, null, expectedFrom, null, false, null);

        assertThat(message.from()).isEqualTo(expectedFrom);
    }

    @Test
    void shouldCheckToGetting() {
        Address expectedTo = createAddress(Faker.str_().random());
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
        Content expectedContent = createContent(createType(Faker.str_().random()), Faker.str_().random());
        DefaultMessage message
                = new DefaultMessage(null, null, null, null, false, expectedContent);

        assertThat(message.content()).isEqualTo(expectedContent);
    }

    private Address createAddress(final String value){
        Address address = Mockito.mock(Address.class);
        Mockito
                .when(address.value())
                .thenReturn(value);

        return address;
    }

    private Type createType(final String value){
        Type type = Mockito.mock(Type.class);
        Mockito
                .when(type.value())
                .thenReturn(value);

        return type;
    }

    private Content createContent(final Type type, final String value){
        Content content = Mockito.mock(Content.class);
        Mockito
                .when(content.type())
                .thenReturn(type);
        Mockito
                .when(content.value())
                .thenReturn(value);

        return content;
    }
}