package ru.multa.entia.conversion.impl.pipeline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.pipeline.Pipeline;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.pipeline.PipelineReceiver;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@Slf4j
public class DefaultPublisherPipeline<T extends ConversationItem> implements Pipeline<PublisherTask<T>> {
    @RequiredArgsConstructor
    @Getter
    public enum Code {
        ALREADY_STARTED("default-publisher-pipeline.already-started"),

        ALREADY_STOPPED("default-publisher-pipeline.already-stopped"),

        OFFER_IF_NOT_STARTED("default-publisher-pipeline.offer-if-not-started"),
        OFFER_QUEUE_IS_FULL("default-publisher-pipeline.offer-queue-is-gull");

        private final String value;
    }

    private static final String DEFAULT_BOX_PROCESSOR_THREAD_NAME = "publisher-box-processor-thread";

    private static final Supplier<ExecutorService> DEFAULT_BOX_PROCESSOR_SUPPLIER = () -> Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName(DEFAULT_BOX_PROCESSOR_THREAD_NAME);

        return thread;
    });

    private final AtomicBoolean alive = new AtomicBoolean(false);
    private final BlockingQueue<PipelineBox<PublisherTask<T>>> queue;
    private final PipelineReceiver<PublisherTask<T>> receiver;
    private final Supplier<ExecutorService> boxProcessorSupplier;

    private UUID sessionId;
    private ExecutorService boxProcessor;

    public DefaultPublisherPipeline(final BlockingQueue<PipelineBox<PublisherTask<T>>> queue,
                                    final PipelineReceiver<PublisherTask<T>> receiver) {
        this(queue, receiver, null);
    }

    public DefaultPublisherPipeline(final BlockingQueue<PipelineBox<PublisherTask<T>>> queue,
                                    final PipelineReceiver<PublisherTask<T>> receiver,
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
    public Result<PublisherTask<T>> offer(final PipelineBox<PublisherTask<T>> box) {
        log.info("The attempt of offer: {}", box.value());

        Code code = Code.OFFER_IF_NOT_STARTED;
        if (alive.get()){
            code = queue.offer(box) ? null : Code.OFFER_QUEUE_IS_FULL;
        }

        return code == null
                ? DefaultResultBuilder.<PublisherTask<T>>ok(box.value())
                : DefaultResultBuilder.<PublisherTask<T>>fail(code.getValue());
    }

    private void processBoxes(){
        log.info("Box processing started");

        while (alive.get()) {
            try {
                PipelineBox<PublisherTask<T>> box = queue.take();
                // TODO: 18.12.2023 !!!
                System.out.println("+++ " + box);
                Result<Object> result = receiver.receive(sessionId, box);
                // TODO: 18.12.2023 !!!
                System.out.println("result: " + result);
            } catch (InterruptedException exception) {
                log.error(exception.getMessage(), exception);
                Thread.currentThread().interrupt();
            }
        }
        log.info("Box processing finished");
    }
}

