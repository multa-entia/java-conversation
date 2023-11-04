package ru.multa.entia.conversion.impl.pipeline;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.publisher.Publisher;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;
import utils.FakerUtil;
import utils.ResultUtil;
import utils.TestHolderReleaseStrategy;
import utils.TestHolderTimeoutStrategy;


import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPublisherPipelineSubscriberTest {

    @Test
    void shouldCheckIdGetting_ifItIsNotSetOnCreation() {
        DefaultPublisherPipelineSubscriber<Message> subscriber = new DefaultPublisherPipelineSubscriber<>(null);
        UUID id = subscriber.getId();

        assertThat(id).isNotNull();
    }

    @Test
    void shouldCheckIdGetting() {
        UUID expectedId = Faker.uuid_().random();
        DefaultPublisherPipelineSubscriber<Message> subscriber = new DefaultPublisherPipelineSubscriber<>(expectedId, null);
        UUID id = subscriber.getId();

        assertThat(id).isEqualTo(expectedId);
    }

    @Test
    void shouldCheckSubscriptionExecution_ifFail() {
        AtomicReference<Object> taskAR = new AtomicReference<>();

        Message expectedMessage = FakerUtil.randomMessage();
        TestHolderTimeoutStrategy expectedTimeoutStrategy = new TestHolderTimeoutStrategy();
        TestHolderReleaseStrategy expectedReleaseStrategy = new TestHolderReleaseStrategy();

        Supplier<TestPublisherTask> taskSupplier = () -> {
            TestPublisherTask task = Mockito.mock(TestPublisherTask.class);
            Mockito.when(task.item()).thenReturn(expectedMessage);
            Mockito.when(task.timeoutStrategy()).thenReturn(expectedTimeoutStrategy);
            Mockito.when(task.releaseStrategy()).thenReturn(expectedReleaseStrategy);

            return task;
        };

        Seed expectedSeed = ResultUtil.seed(Faker.str_().random(), Faker.int_().random(), Faker.long_().random());
        Supplier<TestPublisher> publisherSupplier = () -> {
            TestPublisher publisher = Mockito.mock(TestPublisher.class);
            Mockito
                    .when(publisher.publish(Mockito.any()))
                    .thenAnswer(new Answer<Result<Message>>() {
                        @Override
                        public Result<Message> answer(InvocationOnMock invocation) throws Throwable {
                            taskAR.set(invocation.getArgument(0));
                            return ResultUtil.fail(expectedSeed);
                        }
                    });

            return publisher;
        };

        TestPublisherTask task = taskSupplier.get();
        Result<PublisherTask<Message>> result = new DefaultPublisherPipelineSubscriber<Message>(publisherSupplier.get())
                .give(task);

        assertThat(ResultUtil.isEqual(result, ResultUtil.fail(expectedSeed))).isTrue();

        TestPublisherTask holtTask = (TestPublisherTask) taskAR.get();
        assertThat(holtTask.item()).isEqualTo(expectedMessage);
        assertThat(holtTask.timeoutStrategy()).isEqualTo(expectedTimeoutStrategy);
        assertThat(holtTask.releaseStrategy()).isEqualTo(expectedReleaseStrategy);
    }

    @Test
    void shouldCheckSubscriptionExecution() {
        AtomicReference<Object> taskAR = new AtomicReference<>();

        Message expectedMessage = FakerUtil.randomMessage();
        TestHolderTimeoutStrategy expectedTimeoutStrategy = new TestHolderTimeoutStrategy();
        TestHolderReleaseStrategy expectedReleaseStrategy = new TestHolderReleaseStrategy();

        Supplier<TestPublisherTask> taskSupplier = () -> {
            TestPublisherTask task = Mockito.mock(TestPublisherTask.class);
            Mockito.when(task.item()).thenReturn(expectedMessage);
            Mockito.when(task.timeoutStrategy()).thenReturn(expectedTimeoutStrategy);
            Mockito.when(task.releaseStrategy()).thenReturn(expectedReleaseStrategy);

            return task;
        };

        Supplier<TestPublisher> publisherSupplier = () -> {
            TestPublisher publisher = Mockito.mock(TestPublisher.class);
            Mockito
                    .when(publisher.publish(Mockito.any()))
                    .thenAnswer(new Answer<Result<Message>>() {
                        @Override
                        public Result<Message> answer(InvocationOnMock invocation) throws Throwable {
                            taskAR.set(invocation.getArgument(0));
                            return ResultUtil.ok(expectedMessage);
                        }
                    });

            return publisher;
        };

        TestPublisherTask task = taskSupplier.get();
        Result<PublisherTask<Message>> result = new DefaultPublisherPipelineSubscriber<Message>(publisherSupplier.get())
                .give(task);

        assertThat(result.ok()).isTrue();
        assertThat(result.seed()).isNull();

        TestPublisherTask resultTask = (TestPublisherTask) result.value();
        assertThat(resultTask.item()).isEqualTo(expectedMessage);
        assertThat(resultTask.timeoutStrategy()).isEqualTo(expectedTimeoutStrategy);
        assertThat(resultTask.releaseStrategy()).isEqualTo(expectedReleaseStrategy);

        TestPublisherTask holtTask = (TestPublisherTask) taskAR.get();
        assertThat(holtTask.item()).isEqualTo(expectedMessage);
        assertThat(holtTask.timeoutStrategy()).isEqualTo(expectedTimeoutStrategy);
        assertThat(holtTask.releaseStrategy()).isEqualTo(expectedReleaseStrategy);
    }

    private interface TestPublisherTask extends PublisherTask<Message> {}
    private interface TestPublisher extends Publisher<Message> {}
}