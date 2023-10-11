package ru.multa.entia.conversion.impl.confirmation;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.seed.Seed;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultConfirmationCheckerTest {

    @Test
    void shouldCheckChecking_ifInstanceNull() {
        Seed seed = new DefaultConfirmationChecker().apply(null);

        assertThat(seed.code()).isEqualTo(DefaultConfirmationChecker.Code.INSTANCE_IS_NULL.getValue());
        assertThat(seed.args()).isEmpty();
    }

    @Test
    void shouldCheckChecking_ifFieldIdNull() {
        Object[] expectedArgs = {DefaultConfirmationChecker.Alias.ID.getValue()};
        Message message = createMessage(
                null,
                Faker.uuid_().random(),
                createAddress(Faker.str_().random()),
                createAddress(Faker.str_().random())
        );
        Seed seed = new DefaultConfirmationChecker().apply(message);

        assertThat(seed.code()).isEqualTo(DefaultConfirmationChecker.Code.FIELD_IS_NULL.getValue());
        assertThat(Arrays.equals(seed.args(), expectedArgs)).isTrue();
    }

    @Test
    void shouldCheckChecking_ifFieldConversationNull() {
        Object[] expectedArgs = {DefaultConfirmationChecker.Alias.CONVERSATION.getValue()};
        Message message = createMessage(
                Faker.uuid_().random(),
                null,
                createAddress(Faker.str_().random()),
                createAddress(Faker.str_().random())
        );
        Seed seed = new DefaultConfirmationChecker().apply(message);

        assertThat(seed.code()).isEqualTo(DefaultConfirmationChecker.Code.FIELD_IS_NULL.getValue());
        assertThat(Arrays.equals(seed.args(), expectedArgs)).isTrue();
    }

    @Test
    void shouldCheckChecking_ifFieldFromNull() {
        Object[] expectedArgs = {DefaultConfirmationChecker.Alias.FROM.getValue()};
        Message message = createMessage(
                Faker.uuid_().random(),
                Faker.uuid_().random(),
                null,
                createAddress(Faker.str_().random())
        );
        Seed seed = new DefaultConfirmationChecker().apply(message);

        assertThat(seed.code()).isEqualTo(DefaultConfirmationChecker.Code.FIELD_IS_NULL.getValue());
        assertThat(Arrays.equals(seed.args(), expectedArgs)).isTrue();
    }

    @Test
    void shouldCheckChecking_ifFieldToNull() {
        Object[] expectedArgs = {DefaultConfirmationChecker.Alias.TO.getValue()};
        Message message = createMessage(
                Faker.uuid_().random(),
                Faker.uuid_().random(),
                createAddress(Faker.str_().random()),
                null
        );
        Seed seed = new DefaultConfirmationChecker().apply(message);

        assertThat(seed.code()).isEqualTo(DefaultConfirmationChecker.Code.FIELD_IS_NULL.getValue());
        assertThat(Arrays.equals(seed.args(), expectedArgs)).isTrue();
    }

    @Test
    void shouldCheckChecking_ifAllFieldsNull() {
        Object[] expectedArgs = {
                DefaultConfirmationChecker.Alias.ID.getValue() +
                DefaultConfirmationChecker.Alias.CONVERSATION.getValue() +
                DefaultConfirmationChecker.Alias.FROM.getValue() +
                DefaultConfirmationChecker.Alias.TO.getValue()
        };
        Message message = createMessage(null, null, null,null);
        Seed seed = new DefaultConfirmationChecker().apply(message);

        assertThat(seed.code()).isEqualTo(DefaultConfirmationChecker.Code.FIELD_IS_NULL.getValue());
        assertThat(Arrays.equals(seed.args(), expectedArgs)).isTrue();
    }

    @Test
    void shouldCheckChecking() {
        Message message = createMessage(
                Faker.uuid_().random(),
                Faker.uuid_().random(),
                createAddress(Faker.str_().random()),
                createAddress(Faker.str_().random())
        );
        Seed seed = new DefaultConfirmationChecker().apply(message);

        assertThat(seed).isNull();
    }

    // TODO: 11.10.2023 use lambda
    private Message createMessage(final UUID id, final UUID conversation, final Address from, final Address to){
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.id()).thenReturn(id);
        Mockito.when(message.conversation()).thenReturn(conversation);
        Mockito.when(message.from()).thenReturn(from);
        Mockito.when(message.to()).thenReturn(to);

        return message;
    }

    // TODO: 11.10.2023 use lambda
    private Address createAddress(final String value){
        Address address = Mockito.mock(Address.class);
        Mockito.when(address.value()).thenReturn(value);

        return address;
    }
}