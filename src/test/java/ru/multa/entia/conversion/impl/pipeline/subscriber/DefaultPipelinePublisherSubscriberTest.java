package ru.multa.entia.conversion.impl.pipeline.subscriber;

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
import ru.multa.entia.results.utils.Results;
import utils.FakerUtil;
import utils.ResultUtil;
import utils.TestHolderReleaseStrategy;
import utils.TestHolderTimeoutStrategy;


import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPipelinePublisherSubscriberTest {

    @Test
    void shouldCheckIdGetting_ifItIsNotSetOnCreation() {
        DefaultPipelinePublisherSubscriber<Message> subscriber = new DefaultPipelinePublisherSubscriber<>(null);
        UUID id = subscriber.getId();

        assertThat(id).isNotNull();
    }

    @Test
    void shouldCheckIdGetting() {
        UUID expectedId = Faker.uuid_().random();
        DefaultPipelinePublisherSubscriber<Message> subscriber
                = new DefaultPipelinePublisherSubscriber<>(null, expectedId, null);
        UUID id = subscriber.getId();

        assertThat(id).isEqualTo(expectedId);
    }

    @Test
    void shouldCheckBlock_ifAlreadyBlock() {
        DefaultPipelinePublisherSubscriber<Message> subscriber
                = new DefaultPipelinePublisherSubscriber<>(null, null, null);

        Result<Object> result = subscriber.block();

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(DefaultPipelinePublisherSubscriber.CODES.get(DefaultPipelinePublisherSubscriber.Code.SESSION_ID_ALREADY_RESET))
                .back()
                .compare()).isTrue();
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckBlock() {
        DefaultPipelinePublisherSubscriber<Message> subscriber
                = new DefaultPipelinePublisherSubscriber<>(null, null, Faker.uuid_().random());

        Result<Object> result = subscriber.block();
        Field field = subscriber.getClass().getSuperclass().getDeclaredField("sessionId");
        field.setAccessible(true);

        AtomicReference<UUID> gottenSessionId = (AtomicReference<UUID>) field.get(subscriber);

        assertThat(Results.comparator(result)
                .isSuccess()
                .value(null)
                .compare()).isTrue();
        assertThat(gottenSessionId.get()).isNull();
    }

    @Test
    void shouldCheckBlockOut_ifAlreadyBlockedOut() {
        UUID expectedSessionId = Faker.uuid_().random();
        DefaultPipelinePublisherSubscriber<Message> subscriber
                = new DefaultPipelinePublisherSubscriber<>(null, null, expectedSessionId);

        Result<Object> result = subscriber.blockOut(expectedSessionId);

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(DefaultPipelinePublisherSubscriber.CODES.get(DefaultPipelinePublisherSubscriber.Code.THIS_SESSION_ID_ALREADY_SET))
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckBlockOut_ifSessionIdNull() {
        DefaultPipelinePublisherSubscriber<Message> subscriber
                = new DefaultPipelinePublisherSubscriber<>(null, null, null);

        Result<Object> result = subscriber.blockOut(null);

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(DefaultPipelinePublisherSubscriber.CODES.get(DefaultPipelinePublisherSubscriber.Code.SESSION_ID_ON_BLOCK_OUT_IS_NULL))
                .back()
                .compare()).isTrue();
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckBlockOut() {
        DefaultPipelinePublisherSubscriber<Message> subscriber
                = new DefaultPipelinePublisherSubscriber<>(null, null, null);

        UUID expectedSessionId = Faker.uuid_().random();
        Result<Object> result = subscriber.blockOut(expectedSessionId);
        Field field = subscriber.getClass().getSuperclass().getDeclaredField("sessionId");
        field.setAccessible(true);

        AtomicReference<UUID> gottenSessionId = (AtomicReference<UUID>) field.get(subscriber);

        assertThat(Results.comparator(result)
                .isSuccess()
                .value(null)
                .compare()).isTrue();
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
                = new DefaultPipelinePublisherSubscriber<Message>(null, null, null)
                .give(task, Faker.uuid_().random());

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(DefaultPipelinePublisherSubscriber.CODES.get(DefaultPipelinePublisherSubscriber.Code.SESSION_ID_IS_NOT_SET))
                .back()
                .compare()).isTrue();
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
                = new DefaultPipelinePublisherSubscriber<Message>(null, null, Faker.uuid_().random())
                .give(task, null);

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(DefaultPipelinePublisherSubscriber.CODES.get(DefaultPipelinePublisherSubscriber.Code.DISALLOWED_SESSION_ID))
                .back()
                .compare()).isTrue();
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
                = new DefaultPipelinePublisherSubscriber<Message>(publisherSupplier.get(), null, sessionId)
                .give(task, sessionId);

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(expectedSeed.code())
                .args(expectedSeed.args())
                .back()
                .compare()).isTrue();

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
                = new DefaultPipelinePublisherSubscriber<Message>(publisherSupplier.get(), null, sessionId)
                .give(task, sessionId);

        assertThat(Results.comparator(result)
                .isSuccess()
                .seedsComparator()
                .isNull()
                .back()
                .compare()).isTrue();

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