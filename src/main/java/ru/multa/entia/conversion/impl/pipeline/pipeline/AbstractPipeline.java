package ru.multa.entia.conversion.impl.pipeline.pipeline;

import lombok.extern.slf4j.Slf4j;
import ru.multa.entia.conversion.api.ConversationItem;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Slf4j
abstract public class AbstractPipeline<T extends ConversationItem, TASK> implements Pipeline<TASK> {
    public enum Code {
        ALREADY_STARTED,
        ALREADY_STOPPED,
        OFFER_IF_NOT_STARTED,
        OFFER_QUEUE_IS_FULL
    }

    private static final AtomicInteger threadNameCounter = new AtomicInteger(0);

    private static final Function<ThreadParams, ExecutorService> BOX_PROCESSOR_FUNCTION = params -> Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r);
        thread.setName(params.prefix() + threadNameCounter.incrementAndGet());

        return thread;
    });

    private final AtomicBoolean alive = new AtomicBoolean(false);
    private final BlockingQueue<PipelineBox<TASK>> queue;
    private final PipelineReceiver<TASK> receiver;
    private final ThreadParams threadParams;

    private UUID sessionId;
    private ExecutorService boxProcessor;

    public AbstractPipeline(final BlockingQueue<PipelineBox<TASK>> queue,
                            final PipelineReceiver<TASK> receiver) {
        this(queue, receiver, null);
    }

    public AbstractPipeline(final BlockingQueue<PipelineBox<TASK>> queue,
                            final PipelineReceiver<TASK> receiver,
                            final ThreadParams threadParams) {
        this.queue = queue;
        this.receiver = receiver;
        this.threadParams = Objects.requireNonNullElse(threadParams, new ThreadParams());
    }

    @Override
    public Result<Object> start() {
        log.info("The attempt of starting");
        if (alive.compareAndSet(false, true)){
            log.info("Started");
            sessionId = UUID.randomUUID();
            receiver.blockOut(sessionId);
            boxProcessor = BOX_PROCESSOR_FUNCTION.apply(threadParams);
            boxProcessor.submit(this::processBoxes);
            return DefaultResultBuilder.<Object>ok(null);
        }

        // TODO: 30.12.2023 compute it
        return DefaultResultBuilder.<Object>fail(getCode(Code.ALREADY_STARTED));
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

        // TODO: 30.12.2023 compute it
        return DefaultResultBuilder.<Object>fail(getCode(Code.ALREADY_STOPPED));
    }

    @Override
    public Result<TASK> offer(final PipelineBox<TASK> box) {
        log.info("The attempt of offer: {}", box.value());

        Code code = Code.OFFER_IF_NOT_STARTED;
        if (alive.get()){
            code = queue.offer(box) ? null : Code.OFFER_QUEUE_IS_FULL;
        }
        // TODO: 30.12.2023 compute it
        return code == null
                ? DefaultResultBuilder.<TASK>ok(box.value())
                : DefaultResultBuilder.<TASK>fail(getCode(code));
    }

    private void processBoxes(){
        log.info("Box processing started");

        while (alive.get()) {
            try {
                PipelineBox<TASK> box = queue.take();
                receiver.receive(sessionId, box);
            } catch (InterruptedException exception) {
                log.error(exception.getMessage(), exception);
                Thread.currentThread().interrupt();
            }
        }
        log.info("Box processing finished");
    }

    protected abstract String getCode(Code code);

    public record ThreadParams(String prefix) {
        private static final String DEFAULT_PREFIX = "box-processor-thread";

        public ThreadParams() {
            this(DEFAULT_PREFIX);
        }

        public ThreadParams(final String prefix) {
            this.prefix = prefix != null && !prefix.isBlank()
                    ? prefix
                    : String.format("%s-%s-", DEFAULT_PREFIX, UUID.randomUUID());
        }
    }
}
