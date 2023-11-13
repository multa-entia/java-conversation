package ru.multa.entia.conversion.impl.pipeline;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.multa.entia.conversion.api.block.Blocking;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.pipeline.PipelineReceiver;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPublisherPipelineTest {

    private static final Supplier<TestPipelineReceiver> TEST_PIPELINE_RECEIVER_SUPPLIER = () -> {
        return Mockito.mock(TestPipelineReceiver.class);
    };

    private static final Supplier<TestPublisherTask> TEST_PUBLISHER_TASK_SUPPLIER = () -> {
        return Mockito.mock(TestPublisherTask.class);
    };

    private static final Function<TestPublisherTask, TestPipelineBox> TEST_PIPELINE_BOX_FUNCTION = task -> {
        TestPipelineBox box = Mockito.mock(TestPipelineBox.class);
        Mockito.when(box.value()).thenReturn(task);

        return box;
    };

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
        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>(
                null,
                TEST_PIPELINE_RECEIVER_SUPPLIER.get(),
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
        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>(
                null,
                TEST_PIPELINE_RECEIVER_SUPPLIER.get()
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
        AtomicInteger counter = new AtomicInteger(0);
        Supplier<TestPipelineReceiver> testPipelineReceiverSupplier = () -> {
            TestPipelineReceiver receiver = Mockito.mock(TestPipelineReceiver.class);
            Mockito
                    .when(receiver.receive(Mockito.any(), Mockito.any()))
                    .thenAnswer(new Answer<Result<Object>>() {
                        @Override
                        public Result<Object> answer(InvocationOnMock invocation) throws Throwable {
                            counter.incrementAndGet();
                            return null;
                        }
                    });

            return receiver;
        };

        ArrayBlockingQueue<PipelineBox<PublisherTask<Message>>> queue = new ArrayBlockingQueue<>(1_000);
        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>(
                queue,
                testPipelineReceiverSupplier.get()
        );
        pipeline.start();

        Integer size = Faker.int_().between(10, 10);
        for (int i = 0; i < size; i++) {
            TestPublisherTask task = TEST_PUBLISHER_TASK_SUPPLIER.get();
            Result<PublisherTask<Message>> result = pipeline.offer(TEST_PIPELINE_BOX_FUNCTION.apply(task));

            assertThat(ResultUtil.isEqual(result, ResultUtil.ok(task))).isTrue();
        }

        Thread.sleep(10);

        assertThat(counter.get()).isEqualTo(size);
    }

    @Test
    void shouldCheckOffer_ifQueueIsFull() {
        Supplier<TestPipelineReceiver> testPipelineReceiverSupplier = () -> {
            TestPipelineReceiver receiver = Mockito.mock(TestPipelineReceiver.class);
            Mockito
                    .when(receiver.receive(Mockito.any(), Mockito.any()))
                    .thenAnswer(new Answer<Result<Object>>() {
                        @Override
                        public Result<Object> answer(InvocationOnMock invocation) throws Throwable {
                            Thread.sleep(1_000);
                            return null;
                        }
                    });

            return receiver;
        };

        ArrayBlockingQueue<PipelineBox<PublisherTask<Message>>> queue = new ArrayBlockingQueue<>(1);
        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>(
                queue,
                testPipelineReceiverSupplier.get()
        );
        pipeline.start();

        pipeline.offer(TEST_PIPELINE_BOX_FUNCTION.apply(TEST_PUBLISHER_TASK_SUPPLIER.get()));
        pipeline.offer(TEST_PIPELINE_BOX_FUNCTION.apply(TEST_PUBLISHER_TASK_SUPPLIER.get()));
        Result<PublisherTask<Message>> result = pipeline.offer(TEST_PIPELINE_BOX_FUNCTION.apply(TEST_PUBLISHER_TASK_SUPPLIER.get()));

        assertThat(ResultUtil.isEqual(
                result,
                ResultUtil.fail(DefaultPublisherPipeline.Code.OFFER_QUEUE_IS_FULL.getValue()))).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckStopImpact_onQueue() {
        Supplier<TestPipelineReceiver> testPipelineReceiverSupplier = () -> {
            TestPipelineReceiver receiver = Mockito.mock(TestPipelineReceiver.class);
            Mockito
                    .when(receiver.receive(Mockito.any(), Mockito.any()))
                    .thenAnswer(new Answer<Result<Object>>() {
                        @Override
                        public Result<Object> answer(InvocationOnMock invocation) throws Throwable {
                            Thread.sleep(1_000);
                            return null;
                        }
                    });

            return receiver;
        };

        ArrayBlockingQueue<PipelineBox<PublisherTask<Message>>> queue = new ArrayBlockingQueue<>(10);
        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>(
                queue,
                testPipelineReceiverSupplier.get()
        );

        pipeline.start();
        TestPipelineBox expectedBox = TEST_PIPELINE_BOX_FUNCTION.apply(TEST_PUBLISHER_TASK_SUPPLIER.get());
        pipeline.offer(TEST_PIPELINE_BOX_FUNCTION.apply(TEST_PUBLISHER_TASK_SUPPLIER.get()));
        pipeline.offer(expectedBox);
        pipeline.stop(false);

        assertThat(queue).hasSize(1);
        assertThat(queue.take()).isEqualTo(expectedBox);
    }

    @SneakyThrows
    @Test
    void shouldCheckStopWithClearingImpact_onQueue() {
        ArrayBlockingQueue<PipelineBox<PublisherTask<Message>>> queue = new ArrayBlockingQueue<>(10);
        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>(
                queue,
                TEST_PIPELINE_RECEIVER_SUPPLIER.get()
        );

        pipeline.start();
        TestPipelineBox expectedBox = TEST_PIPELINE_BOX_FUNCTION.apply(TEST_PUBLISHER_TASK_SUPPLIER.get());
        pipeline.offer(expectedBox);
        pipeline.stop(true);

        assertThat(queue).isEmpty();
    }

    @SneakyThrows
    @Test
    void shouldCheckPipelineExecution() {
        AtomicInteger counter = new AtomicInteger(0);
        Supplier<TestPipelineReceiver> testPipelineReceiverSupplier = () -> {
            TestPipelineReceiver receiver = Mockito.mock(TestPipelineReceiver.class);
            Mockito
                    .when(receiver.receive(Mockito.any(), Mockito.any()))
                    .thenAnswer(new Answer<Result<Object>>() {
                        @Override
                        public Result<Object> answer(InvocationOnMock invocation) throws Throwable {
                            counter.incrementAndGet();
                            return null;
                        }
                    });

            return receiver;
        };

        ArrayBlockingQueue<PipelineBox<PublisherTask<Message>>> queue = new ArrayBlockingQueue<>(1_000);
        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>(
                queue,
                testPipelineReceiverSupplier.get()
        );

        Integer quantity = Faker.int_().between(100, 200);

        pipeline.start();
        for (int i = 0; i < quantity; i++) {
            pipeline.offer(TEST_PIPELINE_BOX_FUNCTION.apply(TEST_PUBLISHER_TASK_SUPPLIER.get()));
        }
        pipeline.stop(false);

        Thread.sleep(10);

        assertThat(counter.get()).isEqualTo(quantity);
    }

    private interface TestPipelineReceiver extends PipelineReceiver<PublisherTask<Message>> {}
    private interface TestPublisherTask extends PublisherTask<Message> {}
    private interface TestPipelineBox extends PipelineBox<PublisherTask<Message>> {}
}