package ru.multa.entia.conversion.impl.pipeline;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.pipeline.PipelineReceiver;
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

        AtomicReference<Object> sessionIdHolder = new AtomicReference<>();
        Supplier<TestPipelineReceiver> testPipelineReceiverSupplier = () -> {
            TestPipelineReceiver receiver = Mockito.mock(TestPipelineReceiver.class);
            Mockito
                    .when(receiver.blockOut(Mockito.any()))
                    .thenAnswer(new Answer<Result<Object>>() {
                        @Override
                        public Result<Object> answer(InvocationOnMock invocation) throws Throwable {
                            sessionIdHolder.set(invocation.getArgument(0));
                            return null;
                        }
                    });

            return receiver;
        };

        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>(
                null, testPipelineReceiverSupplier.get(), executorServiceSupplier
        );
        Result<Object> result = pipeline.start();

        Field field = pipeline.getClass().getDeclaredField("alive");
        field.setAccessible(true);
        AtomicBoolean gottenAlive = (AtomicBoolean) field.get(pipeline);

        field = pipeline.getClass().getDeclaredField("sessionId");
        field.setAccessible(true);
        Object sessionId = field.get(pipeline);

        field = pipeline.getClass().getDeclaredField("boxProcessor");
        field.setAccessible(true);
        Object boxProcessor = field.get(pipeline);

        assertThat(ResultUtil.isEqual(result, ResultUtil.ok(null))).isTrue();
        assertThat(gottenAlive).isTrue();
        assertThat(sessionId).isNotNull();
        assertThat(boxProcessor).isNotNull();
        assertThat(submitHolder.get()).isNotNull();
        assertThat(sessionIdHolder.get()).isEqualTo(sessionId);
    }

    @SneakyThrows
    @Test
    void shouldCheckStart_ifAlreadyStarted() {
        Supplier<TestPipelineReceiver> testPipelineReceiverSupplier = () -> {
            return Mockito.mock(TestPipelineReceiver.class);
        };

        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>(
                null,
                testPipelineReceiverSupplier.get(),
                null
        );
        pipeline.start();
        Result<Object> result = pipeline.start();

        assertThat(ResultUtil.isEqual(result, ResultUtil.fail(DefaultPublisherPipeline.Code.ALREADY_STARTED.getValue())))
                .isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckStop_ifAlreadyStopped() {
        // TODO: 09.11.2023 move to const 
        Supplier<TestPipelineReceiver> testPipelineReceiverSupplier = () -> {
            return Mockito.mock(TestPipelineReceiver.class);
        };

        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>(
                null,
                testPipelineReceiverSupplier.get()
        );
        Result<Object> result = pipeline.stop(false);

        Field field = pipeline.getClass().getDeclaredField("alive");
        field.setAccessible(true);
        AtomicBoolean gottenAlive = (AtomicBoolean) field.get(pipeline);

        assertThat(ResultUtil.isEqual(result, ResultUtil.fail(DefaultPublisherPipeline.Code.ALREADY_STOPPED.getValue())))
                .isTrue();
        assertThat(gottenAlive).isFalse();
    }

    @SneakyThrows
    @Test
    void shouldCheckStop() {
        AtomicBoolean boxProcessorShutdownIsCall = new AtomicBoolean(false);
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

        AtomicBoolean blockMethodHolder = new AtomicBoolean(false);
        Supplier<TestPipelineReceiver> testPipelineReceiverSupplier = () -> {
            TestPipelineReceiver receiver = Mockito.mock(TestPipelineReceiver.class);
            Mockito
                    .when(receiver.block())
                    .thenAnswer(new Answer<Result<Object>>() {
                        @Override
                        public Result<Object> answer(InvocationOnMock invocation) throws Throwable {
                            blockMethodHolder.set(true);
                            return null;
                        }
                    });

            return receiver;
        };

        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>(
                null,
                testPipelineReceiverSupplier.get(),
                boxProcessorSupplier
        );
        pipeline.start();
        Result<Object> result = pipeline.stop(false);

        Field field = pipeline.getClass().getDeclaredField("alive");
        field.setAccessible(true);
        AtomicBoolean gottenAlive = (AtomicBoolean) field.get(pipeline);

        field = pipeline.getClass().getDeclaredField("sessionId");
        field.setAccessible(true);
        Object sessionId = field.get(pipeline);

        field = pipeline.getClass().getDeclaredField("boxProcessor");
        field.setAccessible(true);
        Object boxProcessor = field.get(pipeline);


        assertThat(ResultUtil.isEqual(result, ResultUtil.ok(null)))
                .isTrue();
        assertThat(gottenAlive).isFalse();
        assertThat(sessionId).isNull();
        assertThat(boxProcessor).isNull();
        assertThat(boxProcessorShutdownIsCall).isTrue();
        assertThat(blockMethodHolder).isTrue();
    }

    @Test
    void shouldCheckOffer_ifNotStarted() {
        Supplier<TestPipelineBox> testPipelineBoxSupplier = () -> {
            TestPublisherTask task = Mockito.mock(TestPublisherTask.class);
            TestPipelineBox box = Mockito.mock(TestPipelineBox.class);
            Mockito.when(box.value()).thenReturn(task);

            return box;
        };

        ArrayBlockingQueue<PipelineBox<PublisherTask<Message>>> queue = new ArrayBlockingQueue<>(1_000);
        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>(queue, null);
        Result<PublisherTask<Message>> result = pipeline.offer(testPipelineBoxSupplier.get());

        assertThat(ResultUtil.isEqual(
                result,
                ResultUtil.fail(DefaultPublisherPipeline.Code.OFFER_IF_NOT_STARTED.getValue()))).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckOffer() {
        Supplier<TestPipelineReceiver> testPipelineReceiverSupplier = () -> {
            return Mockito.mock(TestPipelineReceiver.class);
        };

        Supplier<TestPublisherTask> testPublisherTaskSupplier = () -> {
            return Mockito.mock(TestPublisherTask.class);
        };

        Function<TestPublisherTask, TestPipelineBox> testPipelineBoxFunction = task -> {
            TestPipelineBox box = Mockito.mock(TestPipelineBox.class);
            Mockito.when(box.value()).thenReturn(task);

            return box;
        };

        ArrayBlockingQueue<PipelineBox<PublisherTask<Message>>> queue = new ArrayBlockingQueue<>(1_000);
        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>(
                queue,
                testPipelineReceiverSupplier.get()
        );
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
        Supplier<TestPipelineReceiver> testPipelineReceiverSupplier = () -> {
            return Mockito.mock(TestPipelineReceiver.class);
        };

        Supplier<TestPublisherTask> testPublisherTaskSupplier = () -> {
            return Mockito.mock(TestPublisherTask.class);
        };

        Function<TestPublisherTask, TestPipelineBox> testPipelineBoxFunction = task -> {
            TestPipelineBox box = Mockito.mock(TestPipelineBox.class);
            Mockito.when(box.value()).thenReturn(task);

            return box;
        };

        ArrayBlockingQueue<PipelineBox<PublisherTask<Message>>> queue = new ArrayBlockingQueue<>(1);
        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>(
                queue,
                testPipelineReceiverSupplier.get()
        );
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
        Supplier<TestPipelineReceiver> testPipelineReceiverSupplier = () -> {
            return Mockito.mock(TestPipelineReceiver.class);
        };

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
                testPipelineReceiverSupplier.get()
        );

        pipeline.start();
        TestPipelineBox expectedBox = testPipelineBoxFunction.apply(testPublisherTaskSupplier.get());
        pipeline.offer(expectedBox);
        pipeline.stop(false);

        assertThat(queue).hasSize(1);
        assertThat(queue.take()).isEqualTo(expectedBox);
    }

    @SneakyThrows
    @Test
    void shouldCheckStopWithClearingImpact_onQueue() {
        Supplier<TestPipelineReceiver> testPipelineReceiverSupplier = () -> {
            return Mockito.mock(TestPipelineReceiver.class);
        };

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
                testPipelineReceiverSupplier.get()
        );

        pipeline.start();
        TestPipelineBox expectedBox = testPipelineBoxFunction.apply(testPublisherTaskSupplier.get());
        pipeline.offer(expectedBox);
        pipeline.stop(true);

        assertThat(queue).isEmpty();
    }

    @Test
    void shouldCheckPipelineExecution() {
        // TODO: 08.11.2023 ???
    }

    private interface TestPipelineReceiver extends PipelineReceiver<PublisherTask<Message>> {}
    private interface TestPipelineSubscriber extends PipelineSubscriber<PublisherTask<Message>> {}
    private interface TestPublisherTask extends PublisherTask<Message> {}
    private interface TestPipelineBox extends PipelineBox<PublisherTask<Message>> {}
}