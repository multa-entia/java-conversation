package ru.multa.entia.conversion.impl.pipeline;

import lombok.SneakyThrows;
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


import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: 18.11.2023 faked bool
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
        DefaultPublisherPipelineSubscriber<Message> subscriber
                = new DefaultPublisherPipelineSubscriber<>(null, expectedId, null);
        UUID id = subscriber.getId();

        assertThat(id).isEqualTo(expectedId);
    }

    @Test
    void shouldCheckBlock_ifAlreadyBlock() {
        DefaultPublisherPipelineSubscriber<Message> subscriber
                = new DefaultPublisherPipelineSubscriber<>(null, null, null);

        Result<Object> result = subscriber.block();

        assertThat(ResultUtil.isEqual(
                result,
                ResultUtil.fail(DefaultPublisherPipelineSubscriber.Code.SESSION_ID_ALREADY_RESET.getValue()))).isTrue();
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckBlock() {
        DefaultPublisherPipelineSubscriber<Message> subscriber
                = new DefaultPublisherPipelineSubscriber<>(null, null, Faker.uuid_().random());

        Result<Object> result = subscriber.block();
        Field field = subscriber.getClass().getDeclaredField("sessionId");
        field.setAccessible(true);

        AtomicReference<UUID> gottenSessionId = (AtomicReference<UUID>) field.get(subscriber);

        assertThat(ResultUtil.isEqual(result, ResultUtil.ok(null))).isTrue();
        assertThat(gottenSessionId.get()).isNull();
    }

    @Test
    void shouldCheckBlockOut_ifAlreadyBlockedOut() {
        UUID expectedSessionId = Faker.uuid_().random();
        DefaultPublisherPipelineSubscriber<Message> subscriber
                = new DefaultPublisherPipelineSubscriber<>(null, null, expectedSessionId);

        Result<Object> result = subscriber.blockOut(expectedSessionId);

        assertThat(ResultUtil.isEqual(
                result,
                ResultUtil.fail(DefaultPublisherPipelineSubscriber.Code.THIS_SESSION_ID_ALREADY_SET.getValue()))).isTrue();
    }

    @Test
    void shouldCheckBlockOut_ifSessionIdNull() {
        DefaultPublisherPipelineSubscriber<Message> subscriber
                = new DefaultPublisherPipelineSubscriber<>(null, null, null);

        Result<Object> result = subscriber.blockOut(null);

        assertThat(ResultUtil.isEqual(
                result,
                ResultUtil.fail(DefaultPublisherPipelineSubscriber.Code.SESSION_ID_ON_BLOCK_OUT_IS_NULL.getValue()))).isTrue();
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckBlockOut() {
        DefaultPublisherPipelineSubscriber<Message> subscriber
                = new DefaultPublisherPipelineSubscriber<>(null, null, null);

        UUID expectedSessionId = Faker.uuid_().random();
        Result<Object> result = subscriber.blockOut(expectedSessionId);
        Field field = subscriber.getClass().getDeclaredField("sessionId");
        field.setAccessible(true);

        AtomicReference<UUID> gottenSessionId = (AtomicReference<UUID>) field.get(subscriber);

        assertThat(ResultUtil.isEqual(result, ResultUtil.ok(null))).isTrue();
        assertThat(gottenSessionId.get()).isEqualTo(expectedSessionId);
    }

    @Test
    void shouldCheckSubscriptionExecution_ifBlocked() {
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

        TestPublisherTask task = taskSupplier.get();
        Result<PublisherTask<Message>> result
                = new DefaultPublisherPipelineSubscriber<Message>(null, null, null)
                .give(task, Faker.uuid_().random());

        assertThat(ResultUtil.isEqual(
                result,
                ResultUtil.fail(DefaultPublisherPipelineSubscriber.Code.SESSION_ID_IS_NOT_SET.getValue()))).isTrue();
    }

    @Test
    void shouldCheckSubscriptionExecution_ifSessionIdIsNull() {
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

        TestPublisherTask task = taskSupplier.get();
        Result<PublisherTask<Message>> result
                = new DefaultPublisherPipelineSubscriber<Message>(null, null, Faker.uuid_().random())
                .give(task, null);

        assertThat(ResultUtil.isEqual(
                result,
                ResultUtil.fail(DefaultPublisherPipelineSubscriber.Code.DISALLOWED_SESSION_ID.getValue()))).isTrue();
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
        UUID sessionId = Faker.uuid_().random();
        Result<PublisherTask<Message>> result
                = new DefaultPublisherPipelineSubscriber<Message>(publisherSupplier.get(), null, sessionId)
                .give(task, sessionId);

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
        UUID sessionId = Faker.uuid_().random();
        Result<PublisherTask<Message>> result
                = new DefaultPublisherPipelineSubscriber<Message>(publisherSupplier.get(), null, sessionId)
                .give(task, sessionId);

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