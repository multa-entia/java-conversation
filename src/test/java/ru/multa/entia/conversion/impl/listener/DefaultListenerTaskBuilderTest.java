package ru.multa.entia.conversion.impl.listener;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.conversion.api.listener.ListenerTaskBuilder;
import ru.multa.entia.conversion.api.listener.ListenerTaskCreator;
import ru.multa.entia.conversion.api.message.Message;
import utils.FakerUtil;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultListenerTaskBuilderTest {

    private static final ListenerTaskCreator<Message> CREATOR = item -> {
        TestListenerTask task = Mockito.mock(TestListenerTask.class);
        Mockito.when(task.item()).thenReturn(item);

        return task;
    };

    @SneakyThrows
    @Test
    void shouldCheckItemSetting() {
        DefaultListenerTaskBuilder<Message> expectedBuilder = new DefaultListenerTaskBuilder<>();
        Message expectedMessage = FakerUtil.randomMessage();
        ListenerTaskBuilder<Message> builder = expectedBuilder.item(expectedMessage);

        assertThat(builder).isEqualTo(expectedBuilder);

        Field field = builder.getClass().getDeclaredField("item");
        field.setAccessible(true);
        Message message = (Message) field.get(builder);

        assertThat(message).isEqualTo(expectedMessage);
    }

    @Test
    void shouldCheckBuilding() {
        Message expectedItem = FakerUtil.randomMessage();
        ListenerTask<Message> task = new DefaultListenerTaskBuilder<Message>(CREATOR)
                .item(expectedItem)
                .build();

        assertThat(task.item()).isEqualTo(expectedItem);
    }

    private interface TestListenerTask extends ListenerTask<Message> {}
}