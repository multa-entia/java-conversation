package ru.multa.entia.conversion.impl.publisher;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.message.Message;
import utils.FakerUtil;
import utils.TestHolderReleaseStrategy;
import utils.TestHolderTimeoutStrategy;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPublisherTaskTest {

    @Test
    void shouldCheckItemGetting_ifNull() {
        DefaultPublisherTask<Message> task = new DefaultPublisherTask<>(null, null, null);

        assertThat(task.item()).isNull();
    }

    @Test
    void shouldCheckItemGetting() {
        Message expectedMessage = FakerUtil.randomMessage();
        DefaultPublisherTask<Message> task = new DefaultPublisherTask<>(expectedMessage, null, null);

        assertThat(task.item()).isEqualTo(expectedMessage);
    }

    @Test
    void shouldCheckTimeoutStrategyGetting_ifNull() {
        DefaultPublisherTask<Message> task = new DefaultPublisherTask<>(null, null, null);

        assertThat(task.timeoutStrategy()).isNull();
    }

    @Test
    void shouldCheckTimeoutStrategyGetting() {
        TestHolderTimeoutStrategy expectedStrategy = new TestHolderTimeoutStrategy();
        DefaultPublisherTask<Message> task = new DefaultPublisherTask<>(null, expectedStrategy, null);

        assertThat(task.timeoutStrategy()).isEqualTo(expectedStrategy);
    }

    @Test
    void shouldCheckReleaseStrategyGetting_ifNull() {
        DefaultPublisherTask<Message> task = new DefaultPublisherTask<>(null, null, null);

        assertThat(task.releaseStrategy()).isNull();
    }

    @Test
    void shouldCheckReleaseStrategyGetting() {
        TestHolderReleaseStrategy expectedStrategy = new TestHolderReleaseStrategy();
        DefaultPublisherTask<Message> task = new DefaultPublisherTask<>(null, null, expectedStrategy);

        assertThat(task.releaseStrategy()).isEqualTo(expectedStrategy);
    }
}