package ru.multa.entia.conversion.impl.listener;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.conversion.api.message.Message;
import utils.FakerUtil;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultListenerTaskCreatorTest {

    @Test
    void shouldCheckCreation() {
        Message expectedMessage = FakerUtil.randomMessage();
        ListenerTask<Message> task = new DefaultListenerTaskCreator<Message>().create(expectedMessage);

        assertThat(task.item()).isEqualTo(expectedMessage);
    }
}