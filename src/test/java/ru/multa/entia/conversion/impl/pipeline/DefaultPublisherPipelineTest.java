package ru.multa.entia.conversion.impl.pipeline;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.pipeline.PipelineSubscriber;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import utils.ResultUtil;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPublisherPipelineTest {

    @SneakyThrows
    @Test
    void shouldCheckStart() {
        AtomicReference<Object> submitHolder = new AtomicReference<>();
        Supplier<ExecutorService> executorServiceSupplier = () -> {
            ExecutorService service = Mockito.mock(ExecutorService.class);
            Mockito
                    .when(service.submit(Mockito.any(Runnable.class)))
                    .thenAnswer(new Answer<Future<?>>() {
                        @Override
                        public Future<?> answer(InvocationOnMock invocation) throws Throwable {
                            submitHolder.set(invocation.getArgument(0));
                            return null;
                        }
                    });

            return service;
        };

        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>(
                null, executorServiceSupplier, null
        );
        Result<Object> result = pipeline.start();

        Field field = pipeline.getClass().getDeclaredField("alive");
        field.setAccessible(true);
        Boolean gottenAlive = (Boolean) field.get(pipeline);

        field = pipeline.getClass().getDeclaredField("sessionId");
        field.setAccessible(true);
        Object sessionId = field.get(pipeline);

        field = pipeline.getClass().getDeclaredField("boxProcessor");
        field.setAccessible(true);
        Object boxProcessor = field.get(pipeline);

        field = pipeline.getClass().getDeclaredField("boxHandler");
        field.setAccessible(true);
        Object boxHandler = field.get(pipeline);

        assertThat(ResultUtil.isEqual(result, ResultUtil.ok(null))).isTrue();
        assertThat(gottenAlive).isTrue();
        assertThat(sessionId).isNotNull();
        assertThat(boxProcessor).isNotNull();
        assertThat(boxHandler).isNotNull();
        assertThat(submitHolder.get()).isNotNull();
    }

    @SneakyThrows
    @Test
    void shouldCheckStart_ifAlreadyStarted() {
        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>();
        pipeline.start();
        Result<Object> result = pipeline.start();

        Field field = pipeline.getClass().getDeclaredField("alive");
        field.setAccessible(true);
        Boolean gottenAlive = (Boolean) field.get(pipeline);

        assertThat(ResultUtil.isEqual(result, ResultUtil.fail(DefaultPublisherPipeline.Code.ALREADY_STARTED.getValue())))
                .isTrue();
        assertThat(gottenAlive).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckStop_ifAlreadyStopped() {
        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>();
        Result<Object> result = pipeline.stop();

        Field field = pipeline.getClass().getDeclaredField("alive");
        field.setAccessible(true);
        Boolean gottenAlive = (Boolean) field.get(pipeline);

        assertThat(ResultUtil.isEqual(result, ResultUtil.fail(DefaultPublisherPipeline.Code.ALREADY_STOPPED.getValue())))
                .isTrue();
        assertThat(gottenAlive).isFalse();
    }

    @SneakyThrows
    @Test
    void shouldCheckStop() {
        AtomicReference<Boolean> boxProcessorShutdownIsCall = new AtomicReference<>(false);
        Supplier<ExecutorService> boxProcessorSupplier = () -> {
            ExecutorService service = Mockito.mock(ExecutorService.class);
            Mockito
                    .doAnswer(new Answer<Void>() {
                        @Override
                        public Void answer(InvocationOnMock invocation) throws Throwable {
                            boxProcessorShutdownIsCall.set(true);
                            return null;
                        }
                    })
                    .when(service)
                    .shutdown();

            return service;
        };

        AtomicReference<Boolean> boxHandlerShutdownIsCall = new AtomicReference<>(false);
        Supplier<ExecutorService> boxHandlerSupplier = () -> {
            ExecutorService service = Mockito.mock(ExecutorService.class);
            Mockito
                    .doAnswer(new Answer<Void>() {
                        @Override
                        public Void answer(InvocationOnMock invocation) throws Throwable {
                            boxHandlerShutdownIsCall.set(true);
                            return null;
                        }
                    })
                    .when(service)
                    .shutdown();

            return service;
        };

        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>(
                null,
                boxProcessorSupplier,
                boxHandlerSupplier
        );
        pipeline.start();
        Result<Object> result = pipeline.stop();

        Field field = pipeline.getClass().getDeclaredField("alive");
        field.setAccessible(true);
        Boolean gottenAlive = (Boolean) field.get(pipeline);

        field = pipeline.getClass().getDeclaredField("sessionId");
        field.setAccessible(true);
        Object sessionId = field.get(pipeline);

        field = pipeline.getClass().getDeclaredField("boxProcessor");
        field.setAccessible(true);
        Object boxProcessor = field.get(pipeline);

        field = pipeline.getClass().getDeclaredField("boxHandler");
        field.setAccessible(true);
        Object boxHandler = field.get(pipeline);

        assertThat(ResultUtil.isEqual(result, ResultUtil.ok(null)))
                .isTrue();
        assertThat(gottenAlive).isFalse();
        assertThat(sessionId).isNull();
        assertThat(boxProcessor).isNull();
        assertThat(boxHandler).isNull();
        assertThat(boxProcessorShutdownIsCall.get()).isTrue();
        assertThat(boxHandlerShutdownIsCall.get()).isTrue();
    }

    @Test
    void shouldCheckSubscription_itIsStarted() {
        Supplier<TestPipelineSubscriber> pipelineSubscriberSupplier = () -> {
            TestPipelineSubscriber subscriber = Mockito.mock(TestPipelineSubscriber.class);
            Mockito.when(subscriber.getId()).thenReturn(Faker.uuid_().random());

            return subscriber;
        };

        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>();
        pipeline.start();

        Result<PipelineSubscriber<PublisherTask<Message>>> result = pipeline.subscribe(pipelineSubscriberSupplier.get());

        assertThat(ResultUtil.isEqual(
                result,
                ResultUtil.fail(DefaultPublisherPipeline.Code.SUBSCRIPTION_IF_STARTED.getValue()))).isTrue();
    }

    @Test
    void shouldCheckSubscription() {
        Supplier<TestPipelineSubscriber> pipelineSubscriberSupplier = () -> {
            TestPipelineSubscriber subscriber = Mockito.mock(TestPipelineSubscriber.class);
            Mockito.when(subscriber.getId()).thenReturn(Faker.uuid_().random());

            return subscriber;
        };

        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>();

        TestPipelineSubscriber expectedSubscriber = pipelineSubscriberSupplier.get();
        Result<PipelineSubscriber<PublisherTask<Message>>> result = pipeline.subscribe(expectedSubscriber);

        assertThat(ResultUtil.isEqual(
                result,
                ResultUtil.ok(expectedSubscriber))).isTrue();
    }

    @Test
    void shouldCheckSubscription_ifAlreadySubscribe() {
        Function<UUID, TestPipelineSubscriber> pipelineSubscriberFunction = id -> {
            TestPipelineSubscriber subscriber = Mockito.mock(TestPipelineSubscriber.class);
            Mockito.when(subscriber.getId()).thenReturn(id);

            return subscriber;
        };

        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>();

        UUID id = Faker.uuid_().random();
        pipeline.subscribe(pipelineSubscriberFunction.apply(id));
        Result<PipelineSubscriber<PublisherTask<Message>>> result
                = pipeline.subscribe(pipelineSubscriberFunction.apply(id));

        assertThat(ResultUtil.isEqual(
                result,
                ResultUtil.fail(DefaultPublisherPipeline.Code.ALREADY_SUBSCRIBED.getValue()))).isTrue();
    }

    @Test
    void shouldCheckUnsubscription_itIsStarted() {
        Supplier<TestPipelineSubscriber> pipelineSubscriberSupplier = () -> {
            return Mockito.mock(TestPipelineSubscriber.class);
        };

        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>();
        pipeline.start();

        Result<PipelineSubscriber<PublisherTask<Message>>> result = pipeline.unsubscribe(pipelineSubscriberSupplier.get());

        assertThat(ResultUtil.isEqual(
                result,
                ResultUtil.fail(DefaultPublisherPipeline.Code.UNSUBSCRIPTION_IF_STARTED.getValue()))).isTrue();
    }

    @Test
    void shouldCheckUnsubscription() {
        Function<UUID, TestPipelineSubscriber> pipelineSubscriberFunction = id -> {
            TestPipelineSubscriber subscriber = Mockito.mock(TestPipelineSubscriber.class);
            Mockito.when(subscriber.getId()).thenReturn(id);

            return subscriber;
        };

        UUID id = Faker.uuid_().random();
        TestPipelineSubscriber expectedSubscriber = pipelineSubscriberFunction.apply(id);

        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>();
        pipeline.subscribe(expectedSubscriber);
        Result<PipelineSubscriber<PublisherTask<Message>>> result = pipeline.unsubscribe(expectedSubscriber);

        assertThat(ResultUtil.isEqual(result, ResultUtil.ok(expectedSubscriber))).isTrue();
    }

    @Test
    void shouldCheckUnsubscription_ifNotSubscribed() {
        Supplier<TestPipelineSubscriber> pipelineSubscriberSupplier = () -> {
            TestPipelineSubscriber subscriber = Mockito.mock(TestPipelineSubscriber.class);
            Mockito.when(subscriber.getId()).thenReturn(Faker.uuid_().random());

            return subscriber;
        };

        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>();
        Result<PipelineSubscriber<PublisherTask<Message>>> result = pipeline.unsubscribe(pipelineSubscriberSupplier.get());

        assertThat(ResultUtil.isEqual(
                result,
                ResultUtil.fail(DefaultPublisherPipeline.Code.NOT_UNSUBSCRIBED.getValue()))).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckImpactOnSubscriberOnStart() {
        AtomicReference<Object> blockOutHolder = new AtomicReference<>();
        Supplier<TestPipelineSubscriber> subscriberSupplier = () -> {
            TestPipelineSubscriber subscriber = Mockito.mock(TestPipelineSubscriber.class);
            Mockito
                    .when(subscriber.blockOut(Mockito.any()))
                    .thenAnswer(new Answer<Result<Object>>() {
                        @Override
                        public Result<Object> answer(InvocationOnMock invocation) throws Throwable {
                            blockOutHolder.set(invocation.getArgument(0));
                            return null;
                        }
                    });

            return subscriber;
        };

        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>();
        pipeline.subscribe(subscriberSupplier.get());

        pipeline.start();

        Field field = pipeline.getClass().getDeclaredField("sessionId");
        field.setAccessible(true);
        UUID sessionId = (UUID) field.get(pipeline);

        assertThat(blockOutHolder.get()).isEqualTo(sessionId);
    }

    @Test
    void shouldCheckImpactOnSubscriberOnStop() {
        AtomicBoolean blockHolder = new AtomicBoolean(false);
        Supplier<TestPipelineSubscriber> subscriberSupplier = () -> {
            TestPipelineSubscriber subscriber = Mockito.mock(TestPipelineSubscriber.class);
            Mockito
                    .when(subscriber.block())
                    .thenAnswer(new Answer<Result<Object>>() {
                        @Override
                        public Result<Object> answer(InvocationOnMock invocation) throws Throwable {
                            blockHolder.set(true);
                            return null;
                        }
                    });

            return subscriber;
        };

        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>();
        pipeline.subscribe(subscriberSupplier.get());

        pipeline.start();
        pipeline.stop();

        assertThat(blockHolder).isTrue();
    }

    @Test
    void shouldCheckOffer_ifNotStarted() {
        Supplier<TestPipelineBox> testPipelineBoxSupplier = () -> {
            TestPublisherTask task = Mockito.mock(TestPublisherTask.class);
            TestPipelineBox box = Mockito.mock(TestPipelineBox.class);
            Mockito.when(box.value()).thenReturn(task);

            return box;
        };

        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>();
        Result<PublisherTask<Message>> result = pipeline.offer(testPipelineBoxSupplier.get());

        assertThat(ResultUtil.isEqual(
                result,
                ResultUtil.fail(DefaultPublisherPipeline.Code.OFFER_IF_NOT_STARTED.getValue()))).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckOffer() {
        Supplier<TestPublisherTask> testPublisherTaskSupplier = () -> {
            return Mockito.mock(TestPublisherTask.class);
        };

        Function<TestPublisherTask, TestPipelineBox> testPipelineBoxFunction = task -> {
            TestPipelineBox box = Mockito.mock(TestPipelineBox.class);
            Mockito.when(box.value()).thenReturn(task);

            return box;
        };

        ArrayBlockingQueue<PipelineBox<PublisherTask<Message>>> queue = new ArrayBlockingQueue<>(1_000);
        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>(queue, null, null);
        pipeline.start();

        Integer size = Faker.int_().random(10, 20);
        TestPublisherTask[] tasks = new TestPublisherTask[size];
        for (int i = 0; i < size; i++) {
            TestPublisherTask task = testPublisherTaskSupplier.get();
            tasks[i] = task;
            Result<PublisherTask<Message>> result = pipeline.offer(testPipelineBoxFunction.apply(task));

            assertThat(ResultUtil.isEqual(result, ResultUtil.ok(task))).isTrue();
        }

        assertThat(queue.size()).isEqualTo(size);

        for (TestPublisherTask task : tasks) {
            PipelineBox<PublisherTask<Message>> taken = queue.take();
            assertThat(taken.value()).isEqualTo(task);
        }
    }

    @Test
    void shouldCheckOffer_ifQueueIsFull() {
        Supplier<TestPublisherTask> testPublisherTaskSupplier = () -> {
            return Mockito.mock(TestPublisherTask.class);
        };

        Function<TestPublisherTask, TestPipelineBox> testPipelineBoxFunction = task -> {
            TestPipelineBox box = Mockito.mock(TestPipelineBox.class);
            Mockito.when(box.value()).thenReturn(task);

            return box;
        };

        ArrayBlockingQueue<PipelineBox<PublisherTask<Message>>> queue = new ArrayBlockingQueue<>(1);
        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>(queue, null, null);
        pipeline.start();

        pipeline.offer(testPipelineBoxFunction.apply(testPublisherTaskSupplier.get()));
        Result<PublisherTask<Message>> result = pipeline.offer(testPipelineBoxFunction.apply(testPublisherTaskSupplier.get()));

        assertThat(ResultUtil.isEqual(
                result,
                ResultUtil.fail(DefaultPublisherPipeline.Code.OFFER_QUEUE_IS_FULL.getValue()))).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckStopImpact_onQueue() {
        Supplier<TestPublisherTask> testPublisherTaskSupplier = () -> {
            return Mockito.mock(TestPublisherTask.class);
        };

        Function<TestPublisherTask, TestPipelineBox> testPipelineBoxFunction = task -> {
            TestPipelineBox box = Mockito.mock(TestPipelineBox.class);
            Mockito.when(box.value()).thenReturn(task);

            return box;
        };

        ArrayBlockingQueue<PipelineBox<PublisherTask<Message>>> queue = new ArrayBlockingQueue<>(10);
        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>(
                queue,
                null,
                null
        );

        pipeline.start();
        TestPipelineBox expectedBox = testPipelineBoxFunction.apply(testPublisherTaskSupplier.get());
        pipeline.offer(expectedBox);
        pipeline.stop();

        assertThat(queue).hasSize(1);
        assertThat(queue.take()).isEqualTo(expectedBox);
    }

    @SneakyThrows
    @Test
    void shouldCheckStopWithClearingImpact_onQueue() {
        Supplier<TestPublisherTask> testPublisherTaskSupplier = () -> {
            return Mockito.mock(TestPublisherTask.class);
        };

        Function<TestPublisherTask, TestPipelineBox> testPipelineBoxFunction = task -> {
            TestPipelineBox box = Mockito.mock(TestPipelineBox.class);
            Mockito.when(box.value()).thenReturn(task);

            return box;
        };

        ArrayBlockingQueue<PipelineBox<PublisherTask<Message>>> queue = new ArrayBlockingQueue<>(10);
        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>(
                queue,
                null,
                null
        );

        pipeline.start();
        TestPipelineBox expectedBox = testPipelineBoxFunction.apply(testPublisherTaskSupplier.get());
        pipeline.offer(expectedBox);
        pipeline.stopWithClearing();

        assertThat(queue).isEmpty();
    }

    private interface TestPipelineSubscriber extends PipelineSubscriber<PublisherTask<Message>> {}
    private interface TestPublisherTask extends PublisherTask<Message> {}
    private interface TestPipelineBox extends PipelineBox<PublisherTask<Message>> {}
}