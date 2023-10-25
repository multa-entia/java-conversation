package ru.multa.entia.conversion.impl.publisher;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import utils.FakerUtil;
import utils.TestHolderReleaseStrategy;
import utils.TestHolderTimeoutStrategy;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPublisherTaskCreatorTest {

    @Test
    void shouldCheckCreation() {
        Message expectedItem = FakerUtil.randomMessage();
        TestHolderTimeoutStrategy expectedTimeoutStrategy = new TestHolderTimeoutStrategy();
        TestHolderReleaseStrategy expectedReleaseStrategy = new TestHolderReleaseStrategy();
        PublisherTask<Message> task = new DefaultPublisherTaskCreator<Message>().create(
                expectedItem,
                expectedTimeoutStrategy,
                expectedReleaseStrategy
        );

        assertThat(task.item()).isEqualTo(expectedItem);
        assertThat(task.timeoutStrategy()).isEqualTo(expectedTimeoutStrategy);
        assertThat(task.releaseStrategy()).isEqualTo(expectedReleaseStrategy);
    }
}