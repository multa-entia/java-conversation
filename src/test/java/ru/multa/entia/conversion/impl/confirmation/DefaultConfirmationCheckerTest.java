package ru.multa.entia.conversion.impl.confirmation;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.seed.Seed;
import utils.TestAddress;
import utils.TestMessage;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultConfirmationCheckerTest {

    @Test
    void shouldCheckChecking_ifInstanceNull() {
        Seed seed = new DefaultConfirmationChecker().check(null);

        assertThat(seed.code()).isEqualTo(DefaultConfirmationChecker.Code.INSTANCE_IS_NULL.getValue());
        assertThat(seed.args()).isEmpty();
    }

    @Test
    void shouldCheckChecking_ifFieldIdNull() {
        Object[] expectedArgs = {DefaultConfirmationChecker.Alias.ID.getValue()};
        Message message = new TestMessage(
                null,
                Faker.uuid_().random(),
                new TestAddress(Faker.str_().random()),
                new TestAddress(Faker.str_().random()),
                false,
                null
        );
        Seed seed = new DefaultConfirmationChecker().check(message);

        assertThat(seed.code()).isEqualTo(DefaultConfirmationChecker.Code.FIELD_IS_NULL.getValue());
        assertThat(Arrays.equals(seed.args(), expectedArgs)).isTrue();
    }

    @Test
    void shouldCheckChecking_ifFieldConversationNull() {
        Object[] expectedArgs = {DefaultConfirmationChecker.Alias.CONVERSATION.getValue()};
        Message message = new TestMessage(
                Faker.uuid_().random(),
                null,
                new TestAddress(Faker.str_().random()),
                new TestAddress(Faker.str_().random()),
                false,
                null
        );
        Seed seed = new DefaultConfirmationChecker().check(message);

        assertThat(seed.code()).isEqualTo(DefaultConfirmationChecker.Code.FIELD_IS_NULL.getValue());
        assertThat(Arrays.equals(seed.args(), expectedArgs)).isTrue();
    }

    @Test
    void shouldCheckChecking_ifFieldFromNull() {
        Object[] expectedArgs = {DefaultConfirmationChecker.Alias.FROM.getValue()};
        Message message = new TestMessage(
                Faker.uuid_().random(),
                Faker.uuid_().random(),
                null,
                new TestAddress(Faker.str_().random()),
                false,
                null
        );
        Seed seed = new DefaultConfirmationChecker().check(message);

        assertThat(seed.code()).isEqualTo(DefaultConfirmationChecker.Code.FIELD_IS_NULL.getValue());
        assertThat(Arrays.equals(seed.args(), expectedArgs)).isTrue();
    }

    @Test
    void shouldCheckChecking_ifFieldToNull() {
        Object[] expectedArgs = {DefaultConfirmationChecker.Alias.TO.getValue()};
        Message message = new TestMessage(
                Faker.uuid_().random(),
                Faker.uuid_().random(),
                new TestAddress(Faker.str_().random()),
                null,
                false,
                null
        );
        Seed seed = new DefaultConfirmationChecker().check(message);

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
        Message message = new TestMessage(null, null, null,null, false, null);
        Seed seed = new DefaultConfirmationChecker().check(message);

        assertThat(seed.code()).isEqualTo(DefaultConfirmationChecker.Code.FIELD_IS_NULL.getValue());
        assertThat(Arrays.equals(seed.args(), expectedArgs)).isTrue();
    }

    @Test
    void shouldCheckChecking() {
        Message message = new TestMessage(
                Faker.uuid_().random(),
                Faker.uuid_().random(),
                new TestAddress(Faker.str_().random()),
                new TestAddress(Faker.str_().random()),
                false,
                null
        );
        Seed seed = new DefaultConfirmationChecker().check(message);

        assertThat(seed).isNull();
    }
}