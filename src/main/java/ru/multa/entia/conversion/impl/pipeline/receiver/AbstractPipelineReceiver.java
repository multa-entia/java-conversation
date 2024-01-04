package ru.multa.entia.conversion.impl.pipeline.receiver;

import lombok.extern.slf4j.Slf4j;
import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.pipeline.PipelineReceiver;
import ru.multa.entia.conversion.api.pipeline.PipelineSubscriber;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@Slf4j
abstract public class AbstractPipelineReceiver<T extends ConversationItem, TASK> implements PipelineReceiver<TASK> {
    public enum Code {
        ALREADY_BLOCKED_OUT,
        ALREADY_BLOCKED,
        ALREADY_SUBSCRIBED,
        ALREADY_UNSUBSCRIBED,
        IS_BLOCKED,
        INVALID_SESSION_ID,
        NO_ONE_SUBSCRIBER,
        SUBSCRIBER_FAIL;
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    private static final AtomicInteger threadNameCounter = new AtomicInteger(0);

    private static final Function<ThreadParams, ExecutorService> DEFAULT_BOX_HANDLER_FUNCTION = params -> Executors.newFixedThreadPool(
            params.size(),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable runnable) {
                    Thread thread = new Thread(runnable);
                    thread.setName(params.prefix() + threadNameCounter.incrementAndGet());

                    return thread;
                }
            }
    );

    private final AtomicReference<UUID> sessionId = new AtomicReference<>();
    private final ConcurrentMap<UUID, PipelineSubscriber<TASK>> subscribers = new ConcurrentHashMap<>();
    private final ThreadParams threadParams;

    private ExecutorService boxHandler;

    public AbstractPipelineReceiver() {
        this(null);
    }

    public AbstractPipelineReceiver(final ThreadParams threadParams) {
        this.threadParams = Objects.requireNonNullElse(threadParams, new ThreadParams());
    }

    @Override
    public Result<Object> receive(final UUID sessionId, final PipelineBox<TASK> box) {
        log.info("The attempt of receiving : {} {}", sessionId, box);
        if (this.sessionId.get() == null){
            return DefaultResultBuilder.<Object>fail(CR.get(new CodeKey(getClass(), Code.IS_BLOCKED)));
        }

        if (!this.sessionId.get().equals(sessionId)) {
            return DefaultResultBuilder.<Object>fail(CR.get(new CodeKey(getClass(), Code.INVALID_SESSION_ID)));
        }

        if (subscribers.isEmpty()) {
            return DefaultResultBuilder.<Object>fail(CR.get(new CodeKey(getClass(), Code.NO_ONE_SUBSCRIBER)));
        }

        ArrayList<Seed> seeds = new ArrayList<>();
        for (Map.Entry<UUID, PipelineSubscriber<TASK>> entry : subscribers.entrySet()) {
            boxHandler.submit(() -> {
                entry.getValue().give(box.value(), sessionId);
            });
        }

        return DefaultResultBuilder.<Object>ok(null);
    }

    @Override
    public Result<Object> block() {
        log.info("The attempt of blocking {}", sessionId);
        if (sessionId.get() == null) {
            return DefaultResultBuilder.<Object>fail(CR.get(new CodeKey(getClass(), Code.ALREADY_BLOCKED)));
        }

        log.info("It is blocked {}", sessionId);
        sessionId.set(null);
        boxHandler.shutdown();
        boxHandler = null;
        return DefaultResultBuilder.<Object>ok(null);
    }

    @Override
    public Result<Object> blockOut(final UUID sessionId) {
        log.info("The attempt of blocking out {}", sessionId);
        if (this.sessionId.compareAndSet(null, sessionId)) {
            log.info("It is blocked out {}", sessionId);
            boxHandler = DEFAULT_BOX_HANDLER_FUNCTION.apply(threadParams);
            return DefaultResultBuilder.<Object>ok(null);
        }
        return DefaultResultBuilder.<Object>fail(CR.get(new CodeKey(getClass(), Code.ALREADY_BLOCKED_OUT)));
    }

    @Override
    public Result<PipelineSubscriber<TASK>> subscribe(final PipelineSubscriber<TASK> subscriber) {
        log.info("The attempt of subscription: {}", subscriber);
        if (subscribers.putIfAbsent(subscriber.getId(), subscriber) == null){
            log.info("Subscribed: {}", subscriber);
            subscriber.blockOut(sessionId.get());
            return DefaultResultBuilder.<PipelineSubscriber<TASK>>ok(subscriber);
        }

        log.info("Already subscribed: {}", subscriber);
        return DefaultResultBuilder.<PipelineSubscriber<TASK>>fail(CR.get(new CodeKey(getClass(), Code.ALREADY_SUBSCRIBED)));
    }

    @Override
    public Result<PipelineSubscriber<TASK>> unsubscribe(final PipelineSubscriber<TASK> subscriber) {
        log.info("The attempt of unsubscription: {}", subscriber);
        if (subscribers.remove(subscriber.getId()) != null) {
            log.info("Unsubscribed: {}", subscriber);
            subscriber.block();
            return DefaultResultBuilder.<PipelineSubscriber<TASK>>ok(subscriber);
        }

        log.info("Already unsubscribed: {}", subscriber);
        return DefaultResultBuilder.<PipelineSubscriber<TASK>>fail(CR.get(new CodeKey(getClass(), Code.ALREADY_UNSUBSCRIBED)));
    }

    public record ThreadParams(String prefix, int size) {
        private static final String DEFAULT_PREFIX = "box-handler-thread";
        private static final int MIN_SIZE = 1;
        private static final int DEFAULT_SIZE = 8;
        private static final int MAX_SIZE = 32;

        public ThreadParams() {
            this(DEFAULT_PREFIX, DEFAULT_SIZE);
        }

        public ThreadParams(final String prefix, final int size) {
            this.prefix = prefix != null && !prefix.isBlank()
                    ? prefix
                    : String.format("%s-%s-", DEFAULT_PREFIX, UUID.randomUUID());
            this.size = size >= MIN_SIZE && size <= MAX_SIZE ? size : DEFAULT_SIZE;
        }
    }

    public record CodeKey(Class<?> type, Object key) {}
}
