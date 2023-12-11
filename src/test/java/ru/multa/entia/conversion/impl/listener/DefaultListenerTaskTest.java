package ru.multa.entia.conversion.impl.listener;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.message.Message;
import utils.FakerUtil;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultListenerTaskTest {

    @Test
    void shouldCheckGetting_ifNull() {
        DefaultListenerTask<Message> task = new DefaultListenerTask<Message>(null);

        assertThat(task.item()).isNull();
    }

    @Test
    void shouldCheckGetting() {
        Message expectedMessage = FakerUtil.randomMessage();
        DefaultListenerTask<Message> task = new DefaultListenerTask<>(expectedMessage);

        assertThat(task.item()).isEqualTo(expectedMessage);
    }
}