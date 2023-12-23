package ru.multa.entia.conversion.impl.pipeline.subscriber;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.multa.entia.conversion.api.listener.Listener;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.utils.Results;
import utils.FakerUtil;
import utils.ResultUtil;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPipelineListenerSubscriberTest {
    @Test
    void shouldCheckIdGetting_ifItIsNotSetOnCreation() {
        DefaultPipelineListenerSubscriber<Message> subscriber = new DefaultPipelineListenerSubscriber<>(null);

        assertThat(subscriber.getId()).isNotNull();
    }

    @Test
    void shouldCheckIdGetting() {
        UUID expectedId = Faker.uuid_().random();
        DefaultPipelineListenerSubscriber<Message> subscriber =
                new DefaultPipelineListenerSubscriber<>(null, expectedId, null);

        assertThat(subscriber.getId()).isEqualTo(expectedId);
    }

    @Test
    void shouldCheckBlock_ifAlreadyBlocked() {
        DefaultPipelineListenerSubscriber<Message> subscriber = new DefaultPipelineListenerSubscriber<>(null);
        Result<Object> result = subscriber.block();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(DefaultPipelineListenerSubscriber.Code.SESSION_ID_ALREADY_RESET.getValue())
                        .back()
                        .compare()
        ).isTrue();
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckBlock() {
        DefaultPipelineListenerSubscriber<Message> subscriber
                = new DefaultPipelineListenerSubscriber<>(null, null, Faker.uuid_().random());

        Result<Object> result = subscriber.block();
        Field field = subscriber.getClass().getDeclaredField("sessionId");
        field.setAccessible(true);

        AtomicReference<UUID> gottenSessionId = (AtomicReference<UUID>) field.get(subscriber);

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(null)
                        .compare()
        ).isTrue();
        assertThat(gottenSessionId.get()).isNull();
    }

    @Test
    void shouldCheckBlockOut_ifAlreadyBlockedOut() {
        UUID expectedSessionId = Faker.uuid_().random();
        DefaultPipelineListenerSubscriber<Message> subscriber
                = new DefaultPipelineListenerSubscriber<>(null, null, expectedSessionId);

        Result<Object> result = subscriber.blockOut(expectedSessionId);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(DefaultPipelineListenerSubscriber.Code.THIS_SESSION_ID_ALREADY_SET.getValue())
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckBlockOut_ifSessionIdNull() {
        DefaultPipelineListenerSubscriber<Message> subscriber
                = new DefaultPipelineListenerSubscriber<>(null, null, null);

        Result<Object> result = subscriber.blockOut(null);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(DefaultPipelineListenerSubscriber.Code.SESSION_ID_ON_BLOCK_OUT_IS_NULL.getValue())
                        .back()
                        .compare()
        ).isTrue();
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckBlockOut() {
        DefaultPipelineListenerSubscriber<Message> subscriber
                = new DefaultPipelineListenerSubscriber<>(null, null, null);

        UUID expectedSessionId = Faker.uuid_().random();
        Result<Object> result = subscriber.blockOut(expectedSessionId);
        Field field = subscriber.getClass().getDeclaredField("sessionId");
        field.setAccessible(true);

        AtomicReference<UUID> gottenSessionId = (AtomicReference<UUID>) field.get(subscriber);

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(null)
                        .compare()
        ).isTrue();
        assertThat(gottenSessionId.get()).isEqualTo(expectedSessionId);
    }

    @Test
    void shouldCheckSubscriptionExecution_ifBlocked() {
        Message expectedMessage = FakerUtil.randomMessage();

        Supplier<TestListenerTask> taskSupplier = () -> {
            TestListenerTask task = Mockito.mock(TestListenerTask.class);
            Mockito.when(task.item()).thenReturn(expectedMessage);

            return task;
        };

        TestListenerTask task = taskSupplier.get();
        Result<ListenerTask<Message>> result =
                new DefaultPipelineListenerSubscriber<Message>(null, null, null)
                        .give(task, Faker.uuid_().random());

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(DefaultPipelineListenerSubscriber.Code.SESSION_ID_IS_NOT_SET.getValue())
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckSubscriptionExecution_ifSessionIdIsNull() {
        Message expectedMessage = FakerUtil.randomMessage();
        Supplier<TestListenerTask> taskSupplier = () -> {
            TestListenerTask task = Mockito.mock(TestListenerTask.class);
            Mockito.when(task.item()).thenReturn(expectedMessage);

            return task;
        };

        TestListenerTask task = taskSupplier.get();
        Result<ListenerTask<Message>> result =
                new DefaultPipelineListenerSubscriber<Message>(null, null, Faker.uuid_().random())
                        .give(task, null);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(DefaultPipelineListenerSubscriber.Code.DISALLOWED_SESSION_ID.getValue())
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckSubscriptionExecution_ifFail() {
        AtomicReference<Object> taskAR = new AtomicReference<>();
        Message expectedMessage = FakerUtil.randomMessage();
        Supplier<TestListenerTask> taskSupplier = () -> {
            TestListenerTask task = Mockito.mock(TestListenerTask.class);
            Mockito.when(task.item()).thenReturn(expectedMessage);

            return task;
        };

        Seed expectedSeed = ResultUtil.seed(Faker.str_().random(), Faker.int_().random(), Faker.long_().random());
        Supplier<TestListener> listenerSupplier = () -> {
            TestListener listener = Mockito.mock(TestListener.class);
            Mockito
                    .when(listener.listen(Mockito.any()))
                    .thenAnswer(new Answer<Result<Message>>() {
                        @Override
                        public Result<Message> answer(InvocationOnMock invocation) throws Throwable {
                            taskAR.set(invocation.getArgument(0));
                            return ResultUtil.fail(expectedSeed);
                        }
                    });

            return listener;
        };

        TestListenerTask task = taskSupplier.get();
        UUID sessionId = Faker.uuid_().random();
        Result<ListenerTask<Message>> result =
                new DefaultPipelineListenerSubscriber<Message>(listenerSupplier.get(), null, sessionId)
                        .give(task, sessionId);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(expectedSeed.code())
                        .args(expectedSeed.args())
                        .back()
                        .compare()
        ).isTrue();

        TestListenerTask holtTask = (TestListenerTask) taskAR.get();
        assertThat(holtTask.item()).isEqualTo(expectedMessage);
    }

    @Test
    void shouldCheckSubscriptionExecution() {
        AtomicReference<Object> taskAR = new AtomicReference<>();
        Message expectedMessage = FakerUtil.randomMessage();
        Supplier<TestListenerTask> taskSupplier = () -> {
            TestListenerTask task = Mockito.mock(TestListenerTask.class);
            Mockito.when(task.item()).thenReturn(expectedMessage);

            return task;
        };

        Supplier<TestListener> listenerSupplier = () -> {
            TestListener listener = Mockito.mock(TestListener.class);
            Mockito
                    .when(listener.listen(Mockito.any()))
                    .thenAnswer(new Answer<Result<Message>>() {
                        @Override
                        public Result<Message> answer(InvocationOnMock invocation) throws Throwable {
                            taskAR.set(invocation.getArgument(0));
                            return ResultUtil.ok(expectedMessage);
                        }
                    });

            return listener;
        };

        TestListenerTask task = taskSupplier.get();
        UUID sessionId = Faker.uuid_().random();
        Result<ListenerTask<Message>> result =
                new DefaultPipelineListenerSubscriber<Message>(listenerSupplier.get(), null, sessionId)
                        .give(task, sessionId);

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();

        TestListenerTask resultTask = (TestListenerTask) result.value();
        assertThat(resultTask.item()).isEqualTo(expectedMessage);

        TestListenerTask holtTask = (TestListenerTask) taskAR.get();
        assertThat(holtTask.item()).isEqualTo(expectedMessage);
    }

    private interface TestListenerTask extends ListenerTask<Message> {}
    private interface TestListener extends Listener<Message> {}
}