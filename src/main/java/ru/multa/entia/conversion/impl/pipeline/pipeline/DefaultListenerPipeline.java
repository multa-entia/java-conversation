package ru.multa.entia.conversion.impl.pipeline.pipeline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.conversion.api.pipeline.Pipeline;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.pipeline.PipelineReceiver;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

// TODO: 23.12.2023 add abstract
@Slf4j
public class DefaultListenerPipeline<T extends ConversationItem> implements Pipeline<ListenerTask<T>> {
    @RequiredArgsConstructor
    @Getter
    public enum Code {
        ALREADY_STARTED("default-listener-pipeline.already-started"),

        ALREADY_STOPPED("default-listener-pipeline.already-stopped"),

        OFFER_IF_NOT_STARTED("default-listener-pipeline.offer-if-not-started"),
        OFFER_QUEUE_IS_FULL("default-listener-pipeline.offer-queue-is-gull");

        private final String value;
    }

    private static final String DEFAULT_BOX_PROCESSOR_THREAD_NAME = "listener-box-processor-thread";

    private static final Supplier<ExecutorService> DEFAULT_BOX_PROCESSOR_SUPPLIER = () -> Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName(DEFAULT_BOX_PROCESSOR_THREAD_NAME);

        return thread;
    });

    private final AtomicBoolean alive = new AtomicBoolean(false);
    private final BlockingQueue<PipelineBox<ListenerTask<T>>> queue;
    private final PipelineReceiver<ListenerTask<T>> receiver;
    private final Supplier<ExecutorService> boxProcessorSupplier;

    private UUID sessionId;
    private ExecutorService boxProcessor;

    public DefaultListenerPipeline(final BlockingQueue<PipelineBox<ListenerTask<T>>> queue,
                                   final PipelineReceiver<ListenerTask<T>> receiver) {
        this(queue, receiver, null);
    }

    public DefaultListenerPipeline(final BlockingQueue<PipelineBox<ListenerTask<T>>> queue,
                                   final PipelineReceiver<ListenerTask<T>> receiver,
                                   final Supplier<ExecutorService> boxProcessorSupplier) {
        this.queue = queue;
        this.receiver = receiver;
        this.boxProcessorSupplier = Objects.requireNonNullElseGet(boxProcessorSupplier, () -> DEFAULT_BOX_PROCESSOR_SUPPLIER);
    }

    @Override
    public Result<Object> start() {
        log.info("The attempt of starting");
        if (alive.compareAndSet(false, true)){
            log.info("Started");
            sessionId = UUID.randomUUID();
            receiver.blockOut(sessionId);
            boxProcessor = boxProcessorSupplier.get();
            boxProcessor.submit(this::processBoxes);
            return DefaultResultBuilder.<Object>ok(null);
        }

        return DefaultResultBuilder.<Object>fail(Code.ALREADY_STARTED.getValue());
    }

    @Override
    public Result<Object> stop(boolean clear) {
        log.info("The attempt of stopping");
        if (alive.compareAndSet(true, false)){
            log.info("Stopped");
            sessionId = null;
            receiver.block();
            boxProcessor.shutdown();
            boxProcessor = null;
            if (clear){
                queue.clear();
            }
            return DefaultResultBuilder.<Object>ok(null);
        }

        return DefaultResultBuilder.<Object>fail(Code.ALREADY_STOPPED.getValue());
    }

    @Override
    public Result<ListenerTask<T>> offer(PipelineBox<ListenerTask<T>> box) {
        log.info("The attempt of offer: {}", box.value());

        Code code = Code.OFFER_IF_NOT_STARTED;
        if (alive.get()){
            code = queue.offer(box) ? null : Code.OFFER_QUEUE_IS_FULL;
        }

        return code == null
                ? DefaultResultBuilder.<ListenerTask<T>>ok(box.value())
                : DefaultResultBuilder.<ListenerTask<T>>fail(code.getValue());
    }

    private void processBoxes(){
        log.info("Box processing started");

        while (alive.get()) {
            try {
                PipelineBox<ListenerTask<T>> box = queue.take();
                receiver.receive(sessionId, box);
            } catch (InterruptedException exception) {
                log.error(exception.getMessage(), exception);
                Thread.currentThread().interrupt();
            }
        }
        log.info("Box processing finished");
    }
}
