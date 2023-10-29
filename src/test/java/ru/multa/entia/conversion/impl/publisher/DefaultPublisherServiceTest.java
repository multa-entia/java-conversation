package ru.multa.entia.conversion.impl.publisher;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.conversion.api.holder.HolderReleaseStrategy;
import ru.multa.entia.conversion.api.holder.HolderTimeoutStrategy;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.publisher.PublisherService;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.conversion.api.publisher.PublisherTaskBuilder;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import utils.FakerUtil;
import utils.TestHolderReleaseStrategy;
import utils.TestHolderTimeoutStrategy;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPublisherServiceTest {

    @SuppressWarnings("unchecked")
    @Test
    void shouldCheckPublishing_item() {
        Message expectedMessage = FakerUtil.randomMessage();
        TestHolderTimeoutStrategy expectedTimeoutStrategy = new TestHolderTimeoutStrategy();
        TestHolderReleaseStrategy expectedReleaseStrategy = new TestHolderReleaseStrategy();

        Supplier<HolderTimeoutStrategy> timeoutStrategySupplier = () -> {return expectedTimeoutStrategy;};
        Supplier<HolderReleaseStrategy> releaseStrategySupplier = () -> {return expectedReleaseStrategy;};

        AtomicReference<Object> outputAR = new AtomicReference<>();
        Function<PublisherTask<Message>, Result<PublisherTask<Message>>> output = task -> {
            outputAR.set(task);
            return DefaultResultBuilder.<PublisherTask<Message>>ok(task);
        };

        DefaultPublisherService<Message> service = new DefaultPublisherService<>(
                output,
                timeoutStrategySupplier,
                releaseStrategySupplier,
                null,
                null);
        Result<PublisherTask<Message>> result = service.publish(expectedMessage);

        assertThat(result.ok()).isTrue();
        assertThat(result.seed()).isNull();
        assertThat(result.value()).isNotNull();

        PublisherTask<Message> task = result.value();
        assertThat(task.item()).isEqualTo(expectedMessage);
        assertThat(task.timeoutStrategy()).isEqualTo(expectedTimeoutStrategy);
        assertThat(task.releaseStrategy()).isEqualTo(expectedReleaseStrategy);

        PublisherTask<Message> gottenTask = (PublisherTask<Message>) outputAR.get();
        assertThat(gottenTask.item()).isEqualTo(expectedMessage);
        assertThat(gottenTask.timeoutStrategy()).isEqualTo(expectedTimeoutStrategy);
        assertThat(gottenTask.releaseStrategy()).isEqualTo(expectedReleaseStrategy);
    }

    @Test
    void shouldCheckPublishing_task() {
        Message expectedMessage = FakerUtil.randomMessage();
        TestHolderTimeoutStrategy expectedTimeoutStrategy = new TestHolderTimeoutStrategy();
        TestHolderReleaseStrategy expectedReleaseStrategy = new TestHolderReleaseStrategy();

        AtomicReference<Object> outputAR = new AtomicReference<>();
        Function<PublisherTask<Message>, Result<PublisherTask<Message>>> output = task -> {
            outputAR.set(task);
            return DefaultResultBuilder.<PublisherTask<Message>>ok(task);
        };

        DefaultPublisherService<Message> service = new DefaultPublisherService<>(
                output,
                null,
                null,
                null,
                null);

        Supplier<TestPublisherTask> publisherTaskSupplier = () -> {
            TestPublisherTask task = Mockito.mock(TestPublisherTask.class);
            Mockito.when(task.item()).thenReturn(expectedMessage);
            Mockito.when(task.timeoutStrategy()).thenReturn(expectedTimeoutStrategy);
            Mockito.when(task.releaseStrategy()).thenReturn(expectedReleaseStrategy);

            return task;
        };

        Result<PublisherTask<Message>> result = service.publish(publisherTaskSupplier.get());

        assertThat(result.ok()).isTrue();
        assertThat(result.seed()).isNull();
        assertThat(result.value()).isNotNull();

        PublisherTask<Message> task = result.value();
        assertThat(task.item()).isEqualTo(expectedMessage);
        assertThat(task.timeoutStrategy()).isEqualTo(expectedTimeoutStrategy);
        assertThat(task.releaseStrategy()).isEqualTo(expectedReleaseStrategy);

        TestPublisherTask gottenTask = (TestPublisherTask) outputAR.get();
        assertThat(gottenTask.item()).isEqualTo(expectedMessage);
        assertThat(gottenTask.timeoutStrategy()).isEqualTo(expectedTimeoutStrategy);
        assertThat(gottenTask.releaseStrategy()).isEqualTo(expectedReleaseStrategy);
    }

    @Test
    void shouldCheckBuilderGetting() {
        Message expectedMessage = FakerUtil.randomMessage();
        TestHolderTimeoutStrategy expectedTimeoutStrategy = new TestHolderTimeoutStrategy();
        TestHolderReleaseStrategy expectedReleaseStrategy = new TestHolderReleaseStrategy();

        Supplier<TestPublisherTask> publisherTaskSupplier = () -> {
            TestPublisherTask task = Mockito.mock(TestPublisherTask.class);
            Mockito.when(task.item()).thenReturn(expectedMessage);
            Mockito.when(task.timeoutStrategy()).thenReturn(expectedTimeoutStrategy);
            Mockito.when(task.releaseStrategy()).thenReturn(expectedReleaseStrategy);

            return task;
        };
        TestPublisherTask testPublisherTask = publisherTaskSupplier.get();

        Function<PublisherService<Message>, PublisherTaskBuilder<Message>> builderCreator = service -> {
            TestPublisherTaskBuilder builder = Mockito.mock(TestPublisherTaskBuilder.class);
            Mockito.when(builder.build()).thenReturn(testPublisherTask);

            return builder;
        };

        PublisherTaskBuilder<Message> builder = new DefaultPublisherService<Message>(
                null,
                null,
                null,
                builderCreator,
                null)
                .builder();
        assertThat(builder).isNotNull();

        PublisherTask<Message> task = builder.build();
        assertThat(task.item()).isEqualTo(expectedMessage);
        assertThat(task.timeoutStrategy()).isEqualTo(expectedTimeoutStrategy);
        assertThat(task.releaseStrategy()).isEqualTo(expectedReleaseStrategy);
    }

    private interface TestPublisherTask extends PublisherTask<Message> {}
    private interface TestPublisherTaskBuilder extends PublisherTaskBuilder<Message> {}
}