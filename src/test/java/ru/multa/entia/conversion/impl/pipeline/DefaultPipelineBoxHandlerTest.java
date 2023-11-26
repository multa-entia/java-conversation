package ru.multa.entia.conversion.impl.pipeline;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.pipeline.PipelineBoxHandlerTask;
import ru.multa.entia.conversion.api.pipeline.PipelineSubscriber;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.utils.Results;
import utils.ResultUtil;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPipelineBoxHandlerTest {

    @Test
    void shouldCheckHandling_ifTypeIsNull() {
        Result<Object> result = new DefaultPipelineBoxHandler<Message>().handle(null);

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(DefaultPipelineBoxHandler.Code.TASK_IS_NULL.getValue())
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckHandling_ifTypeHasBadType() {
        Supplier<TestPipelineBoxHandlerTask> testPipelineBoxHandlerTaskSupplier = () -> {
            return Mockito.mock(TestPipelineBoxHandlerTask.class);
        };

        Result<Object> result = new DefaultPipelineBoxHandler<Message>()
                .handle(testPipelineBoxHandlerTaskSupplier.get());

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(DefaultPipelineBoxHandler.Code.INVALID_TASK_TYPE.getValue())
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckHandling_ifTaskBoxIsInvalid() {
        DefaultPipelineBoxHandlerTask<Message> task = new DefaultPipelineBoxHandlerTask<>(
                null,
                null,
                null
        );

        Result<Object> result = new DefaultPipelineBoxHandler<Message>().handle(task);

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(DefaultPipelineBoxHandler.Code.INVALID_TASK_BOX.getValue())
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckHandling_ifTaskBoxValueIsInvalid() {
        Supplier<TestPipelineBox> testPipelineBoxSupplier = () -> {
            TestPipelineBox box = Mockito.mock(TestPipelineBox.class);
            Mockito.when(box.value()).thenReturn(null);

            return box;
        };

        DefaultPipelineBoxHandlerTask<Message> task = new DefaultPipelineBoxHandlerTask<>(
                testPipelineBoxSupplier.get(),
                null,
                null
        );

        Result<Object> result = new DefaultPipelineBoxHandler<Message>().handle(task);

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(DefaultPipelineBoxHandler.Code.INVALID_TASK_BOX_VALUE.getValue())
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckHandling_ifTaskSessionIdIsInvalid() {
        Supplier<TestPipelineBox> testPipelineBoxSupplier = () -> {
            TestPublisherTask publisherTask = Mockito.mock(TestPublisherTask.class);

            TestPipelineBox box = Mockito.mock(TestPipelineBox.class);
            Mockito.when(box.value()).thenReturn(publisherTask);

            return box;
        };

        DefaultPipelineBoxHandlerTask<Message> task = new DefaultPipelineBoxHandlerTask<>(
                testPipelineBoxSupplier.get(),
                null,
                null
        );

        Result<Object> result = new DefaultPipelineBoxHandler<Message>().handle(task);

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(DefaultPipelineBoxHandler.Code.INVALID_TASK_SESSION_ID.getValue())
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckHandling_ifActorIsInvalid_null() {
        Supplier<TestPipelineBox> testPipelineBoxSupplier = () -> {
            TestPublisherTask publisherTask = Mockito.mock(TestPublisherTask.class);

            TestPipelineBox box = Mockito.mock(TestPipelineBox.class);
            Mockito.when(box.value()).thenReturn(publisherTask);

            return box;
        };

        DefaultPipelineBoxHandlerTask<Message> task = new DefaultPipelineBoxHandlerTask<>(
                testPipelineBoxSupplier.get(),
                null,
                Faker.uuid_().random()
        );

        Result<Object> result = new DefaultPipelineBoxHandler<Message>().handle(task);

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(DefaultPipelineBoxHandler.Code.INVALID_ACTOR.getValue())
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckHandling_ifActorIsInvalid_empty() {
        Supplier<TestPipelineBox> testPipelineBoxSupplier = () -> {
            TestPublisherTask publisherTask = Mockito.mock(TestPublisherTask.class);

            TestPipelineBox box = Mockito.mock(TestPipelineBox.class);
            Mockito.when(box.value()).thenReturn(publisherTask);

            return box;
        };

        DefaultPipelineBoxHandlerTask<Message> task = new DefaultPipelineBoxHandlerTask<>(
                testPipelineBoxSupplier.get(),
                new HashMap<>(),
                Faker.uuid_().random()
        );

        Result<Object> result = new DefaultPipelineBoxHandler<Message>().handle(task);

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(DefaultPipelineBoxHandler.Code.INVALID_ACTOR.getValue())
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckHandling_ifFailGiving() {
        Function<Result<PublisherTask<Message>>, PipelineSubscriber<PublisherTask<Message>>> subscriberFunction = result -> {
            TestPipelineSubscriber subscriber = Mockito.mock(TestPipelineSubscriber.class);
            Mockito.when(subscriber.give(Mockito.any(), Mockito.any())).thenReturn(result);

            return subscriber;
        };

        Supplier<TestPipelineBox> testPipelineBoxSupplier = () -> {
            TestPublisherTask publisherTask = Mockito.mock(TestPublisherTask.class);

            TestPipelineBox box = Mockito.mock(TestPipelineBox.class);
            Mockito.when(box.value()).thenReturn(publisherTask);

            return box;
        };

        String expectedSeedCode = Faker.str_().random();
        HashMap<UUID, PipelineSubscriber<PublisherTask<Message>>> actor = new HashMap<>(){{
            put(Faker.uuid_().random(), subscriberFunction.apply(ResultUtil.ok(null)));
            put(Faker.uuid_().random(), subscriberFunction.apply(ResultUtil.fail(expectedSeedCode)));
        }};
        DefaultPipelineBoxHandlerTask<Message> task = new DefaultPipelineBoxHandlerTask<>(
                testPipelineBoxSupplier.get(),
                actor,
                Faker.uuid_().random()
        );

        Result<Object> result = new DefaultPipelineBoxHandler<Message>().handle(task);

        assertThat(Results.comparator(result)
                .isFail()
                .value(null)
                .seedsComparator()
                .code(DefaultPipelineBoxHandler.Code.FAIL_GIVING.getValue())
                .back()
                .compare()).isTrue();

        Object[] args = result.seed().args();
        assertThat(args.length).isEqualTo(1);
    }

    @Test
    void shouldCheckHandling() {
        AtomicReference<PublisherTask<Message>> valueHolder = new AtomicReference<>();
        AtomicReference<UUID> sessionIdHolder = new AtomicReference<>();
        Function<Result<PublisherTask<Message>>, PipelineSubscriber<PublisherTask<Message>>> subscriberFunction = result -> {
            TestPipelineSubscriber subscriber = Mockito.mock(TestPipelineSubscriber.class);
            Mockito
                    .when(subscriber.give(Mockito.any(), Mockito.any()))
                    .thenAnswer(new Answer<Result<PublisherTask<Message>>>() {
                        @Override
                        public Result<PublisherTask<Message>> answer(InvocationOnMock invocation) throws Throwable {
                            valueHolder.set(invocation.getArgument(0));
                            sessionIdHolder.set(invocation.getArgument(1));
                            return result;
                        }
                    });

            return subscriber;
        };

        Supplier<TestPublisherTask> testPublisherTaskSupplier = () -> {
            return Mockito.mock(TestPublisherTask.class);
        };

        Function<TestPublisherTask, TestPipelineBox> testPipelineBoxFunction = task -> {
            TestPipelineBox box = Mockito.mock(TestPipelineBox.class);
            Mockito.when(box.value()).thenReturn(task);

            return box;
        };

        UUID expectedSessionId = Faker.uuid_().random();
        TestPublisherTask expectedPublisherTask = testPublisherTaskSupplier.get();

        HashMap<UUID, PipelineSubscriber<PublisherTask<Message>>> actor = new HashMap<>(){{
            put(Faker.uuid_().random(), subscriberFunction.apply(ResultUtil.ok(expectedPublisherTask)));
        }};
        DefaultPipelineBoxHandlerTask<Message> task = new DefaultPipelineBoxHandlerTask<>(
                testPipelineBoxFunction.apply(expectedPublisherTask),
                actor,
                expectedSessionId
        );

        Result<Object> result = new DefaultPipelineBoxHandler<Message>().handle(task);

        assertThat(Results.comparator(result).isSuccess().value(null).compare()).isTrue();
        assertThat(valueHolder.get()).isEqualTo(expectedPublisherTask);
        assertThat(sessionIdHolder.get()).isEqualTo(expectedSessionId);
    }

    private interface TestPipelineBoxHandlerTask extends PipelineBoxHandlerTask<PublisherTask<Message>> {}
    private interface TestPipelineBox extends PipelineBox<PublisherTask<Message>> {}
    private interface TestPublisherTask extends PublisherTask<Message> {}
    private interface TestPipelineSubscriber extends PipelineSubscriber<PublisherTask<Message>> {}
}