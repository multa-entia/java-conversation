package ru.multa.entia.conversion.impl.message;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.fakers.impl.Faker;
import utils.TestAddress;
import utils.TestContent;
import utils.TestType;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: 18.11.2023 faked bool
class DefaultMessageCreatorTest {

    @Test
    void shouldCheckCreation() {
        UUID expectedId = Faker.uuid_().random();
        UUID expectedConversation = Faker.uuid_().random();
        TestAddress expectedFrom = new TestAddress(Faker.str_().random());
        TestAddress expectedTo = new TestAddress(Faker.str_().random());
        boolean expectedConfirm = true;
        TestContent expectedContent = new TestContent(new TestType(Faker.str_().random()), Faker.str_().random());

        Message message = new DefaultMessageCreator().create(
                expectedId,
                expectedConversation,
                expectedFrom,
                expectedTo,
                expectedConfirm,
                expectedContent
        );

        assertThat(message.id()).isEqualTo(expectedId);
        assertThat(message.conversation()).isEqualTo(expectedConversation);
        assertThat(message.from()).isEqualTo(expectedFrom);
        assertThat(message.to()).isEqualTo(expectedTo);
        assertThat(message.confirm()).isEqualTo(expectedConfirm);
        assertThat(message.content()).isEqualTo(expectedContent);
    }
}