package ru.multa.entia.conversion.impl.pipeline.pipeline;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.pipeline.PipelineReceiver;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import java.lang.reflect.Field;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultListenerPipelineTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    private static final Supplier<TestPipelineReceiver> TEST_PIPELINE_RECEIVER_SUPPLIER = () -> {
        return Mockito.mock(TestPipelineReceiver.class);
    };

    private static final Supplier<TestListenerTask> TEST_LISTENER_TASK_SUPPLIER = () -> {
        return Mockito.mock(TestListenerTask.class);
    };

    private static final Function<TestListenerTask, TestPipelineBox> TEST_PIPELINE_BOX_FUNCTION = task -> {
        TestPipelineBox box = Mockito.mock(TestPipelineBox.class);
        Mockito.when(box.value()).thenReturn(task);

        return box;
    };

    @SneakyThrows
    @Test
    void shouldCheckStart() {
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

        DefaultListenerPipeline<Message> pipeline = new DefaultListenerPipeline<>(
                null,
                testPipelineReceiverSupplier.get()
        );
        Result<Object> result = pipeline.start();

        Field field = pipeline.getClass().getSuperclass().getDeclaredField("alive");
        field.setAccessible(true);
        AtomicBoolean gottenAlive = (AtomicBoolean) field.get(pipeline);

        field = pipeline.getClass().getSuperclass().getDeclaredField("sessionId");
        field.setAccessible(true);
        Object sessionId = field.get(pipeline);

        assertThat(Results.comparator(result).isSuccess().value(null).compare()).isTrue();
        assertThat(gottenAlive).isTrue();
        assertThat(sessionId).isNotNull();
        assertThat(sessionIdHolder.get()).isEqualTo(sessionId);
    }

    @SneakyThrows
    @Test
    void shouldCheckStart_ifAlreadyStarted() {
        DefaultListenerPipeline<Message> pipeline = new DefaultListenerPipeline<>(
                null,
                TEST_PIPELINE_RECEIVER_SUPPLIER.get(),
                null
        );
        pipeline.start();
        Result<Object> result = pipeline.start();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(CR.get(new AbstractPipeline.CodeKey(DefaultListenerPipeline.class, AbstractPipeline.Code.ALREADY_STARTED)))
                        .back()
                        .compare()
        ).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckStop_ifAlreadyStopped() {
        DefaultListenerPipeline<Message> pipeline = new DefaultListenerPipeline<>(
                null,
                TEST_PIPELINE_RECEIVER_SUPPLIER.get()
        );

        Result<Object> result = pipeline.stop(false);

        Field field = pipeline.getClass().getSuperclass().getDeclaredField("alive");
        field.setAccessible(true);
        AtomicBoolean gottenAlive = (AtomicBoolean) field.get(pipeline);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(CR.get(new AbstractPipeline.CodeKey(DefaultListenerPipeline.class, AbstractPipeline.Code.ALREADY_STOPPED)))
                        .back()
                        .compare()
        ).isTrue();
        assertThat(gottenAlive).isFalse();
    }

    @SneakyThrows
    @Test
    void shouldCheckStop() {
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

        DefaultListenerPipeline<Message> pipeline = new DefaultListenerPipeline<>(
                null,
                testPipelineReceiverSupplier.get()
        );
        pipeline.start();
        Result<Object> result = pipeline.stop(false);

        Field field = pipeline.getClass().getSuperclass().getDeclaredField("alive");
        field.setAccessible(true);
        AtomicBoolean gottenAlive = (AtomicBoolean) field.get(pipeline);

        field = pipeline.getClass().getSuperclass().getDeclaredField("sessionId");
        field.setAccessible(true);
        Object sessionId = field.get(pipeline);

        assertThat(Results.comparator(result).isSuccess().value(null).compare()).isTrue();
        assertThat(gottenAlive).isFalse();
        assertThat(sessionId).isNull();
        assertThat(blockMethodHolder).isTrue();
    }

    @Test
    void shouldCheckOffer_ifNotStarted() {
        Supplier<TestPipelineBox> testPipelineBoxSupplier = () -> {
            TestListenerTask task = Mockito.mock(TestListenerTask.class);
            TestPipelineBox box = Mockito.mock(TestPipelineBox.class);
            Mockito.when(box.value()).thenReturn(task);

            return box;
        };

        ArrayBlockingQueue<PipelineBox<ListenerTask<Message>>> queue = new ArrayBlockingQueue<>(1_000);
        DefaultListenerPipeline<Message> pipeline = new DefaultListenerPipeline<>(queue,null);
        Result<ListenerTask<Message>> result = pipeline.offer(testPipelineBoxSupplier.get());

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(CR.get(new AbstractPipeline.CodeKey(DefaultListenerPipeline.class, AbstractPipeline.Code.OFFER_IF_NOT_STARTED)))
                        .back()
                        .compare()
        ).isTrue();
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

        ArrayBlockingQueue<PipelineBox<ListenerTask<Message>>> queue = new ArrayBlockingQueue<>(1_000);
        DefaultListenerPipeline<Message> pipeline = new DefaultListenerPipeline<>(
                queue,
                testPipelineReceiverSupplier.get()
        );
        pipeline.start();

        Integer size = Faker.int_().between(10, 10);
        for (int i = 0; i < size; i++) {
            TestListenerTask task = TEST_LISTENER_TASK_SUPPLIER.get();
            Result<ListenerTask<Message>> result = pipeline.offer(TEST_PIPELINE_BOX_FUNCTION.apply(task));

            assertThat(Results.comparator(result).isSuccess().value(task).compare()).isTrue();
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

        ArrayBlockingQueue<PipelineBox<ListenerTask<Message>>> queue = new ArrayBlockingQueue<>(1);
        DefaultListenerPipeline<Message> pipeline = new DefaultListenerPipeline<>(
                queue,
                testPipelineReceiverSupplier.get()
        );
        pipeline.start();

        pipeline.offer(TEST_PIPELINE_BOX_FUNCTION.apply(TEST_LISTENER_TASK_SUPPLIER.get()));
        pipeline.offer(TEST_PIPELINE_BOX_FUNCTION.apply(TEST_LISTENER_TASK_SUPPLIER.get()));
        Result<ListenerTask<Message>> result = pipeline.offer(TEST_PIPELINE_BOX_FUNCTION.apply(TEST_LISTENER_TASK_SUPPLIER.get()));

        CodeRepository cr = CR;

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(CR.get(new AbstractPipeline.CodeKey(DefaultListenerPipeline.class, AbstractPipeline.Code.OFFER_QUEUE_IS_FULL)))
                        .back()
                        .compare()
        ).isTrue();
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

        ArrayBlockingQueue<PipelineBox<ListenerTask<Message>>> queue = new ArrayBlockingQueue<>(10);
        DefaultListenerPipeline<Message> pipeline = new DefaultListenerPipeline<>(
                queue,
                testPipelineReceiverSupplier.get()
        );

        pipeline.start();
        TestPipelineBox expectedBox = TEST_PIPELINE_BOX_FUNCTION.apply(TEST_LISTENER_TASK_SUPPLIER.get());
        pipeline.offer(TEST_PIPELINE_BOX_FUNCTION.apply(TEST_LISTENER_TASK_SUPPLIER.get()));
        pipeline.offer(expectedBox);
        pipeline.stop(false);

        assertThat(queue).hasSize(1);
        assertThat(queue.take()).isEqualTo(expectedBox);
    }

    @SneakyThrows
    @Test
    void shouldCheckStopWithClearingImpact_onQueue() {
        ArrayBlockingQueue<PipelineBox<ListenerTask<Message>>> queue = new ArrayBlockingQueue<>(10);
        DefaultListenerPipeline<Message> pipeline = new DefaultListenerPipeline<>(
                queue,
                TEST_PIPELINE_RECEIVER_SUPPLIER.get()
        );

        pipeline.start();
        TestPipelineBox expectedBox = TEST_PIPELINE_BOX_FUNCTION.apply(TEST_LISTENER_TASK_SUPPLIER.get());
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

        ArrayBlockingQueue<PipelineBox<ListenerTask<Message>>> queue = new ArrayBlockingQueue<>(1_000);
        DefaultListenerPipeline<Message> pipeline = new DefaultListenerPipeline<>(
                queue,
                testPipelineReceiverSupplier.get()
        );

        Integer quantity = Faker.int_().between(100, 200);

        pipeline.start();
        for (int i = 0; i < quantity; i++) {
            pipeline.offer(TEST_PIPELINE_BOX_FUNCTION.apply(TEST_LISTENER_TASK_SUPPLIER.get()));
        }
        pipeline.stop(false);

        Thread.sleep(10);

        assertThat(counter.get()).isEqualTo(quantity);
    }

    private interface TestPipelineReceiver extends PipelineReceiver<ListenerTask<Message>> {}
    private interface TestListenerTask extends ListenerTask<Message> {}
    private interface TestPipelineBox extends PipelineBox<ListenerTask<Message>> {}
}