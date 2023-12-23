package ru.multa.entia.conversion.impl.pipeline.receiver;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.pipeline.PipelineReceiver;
import ru.multa.entia.conversion.api.pipeline.PipelineSubscriber;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

// TODO: 23.12.2023 add abstract
@Slf4j
public class DefaultPublisherPipelineReceiver<T extends ConversationItem> implements PipelineReceiver<PublisherTask<T>> {
    @RequiredArgsConstructor
    @Getter
    public enum Code {
        ALREADY_BLOCKED_OUT("default-publisher-receiver.already-blocked-out"),
        ALREADY_BLOCKED("default-publisher-receiver.already-blocked"),
        ALREADY_SUBSCRIBED("default-publisher-receiver.already-subscribed"),
        ALREADY_UNSUBSCRIBED("default-publisher-receiver.already-unsubscribed"),
        IS_BLOCKED("default-publisher-receiver.is-blocked"),
        INVALID_SESSION_ID("default-publisher-receiver.invalid-session-id"),
        NO_ONE_SUBSCRIBER("default-publisher-receiver.no-one-subscriber"),
        SUBSCRIBER_FAIL("default-publisher-receiver.subscriber-fail");

        private final String value;
    }

    private static final String DEFAULT_BOX_HANDLER_THREAD_PREFIX = "publisher-box-handler-thread-";
    private static final int BOX_HANDLER_THREAD_LIMIT = 8;

    private static final Supplier<ExecutorService> DEFAULT_BOX_HANDLER_SUPPLIER = () -> Executors.newFixedThreadPool(
            BOX_HANDLER_THREAD_LIMIT,
            new ThreadFactory() {
                private final AtomicInteger threadNameCounter = new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable runnable) {
                    Thread thread = new Thread(runnable);
                    thread.setName(DEFAULT_BOX_HANDLER_THREAD_PREFIX + threadNameCounter.incrementAndGet());

                    return thread;
                }
            }
    );

    private final AtomicReference<UUID> sessionId = new AtomicReference<>();
    private final ConcurrentMap<UUID, PipelineSubscriber<PublisherTask<T>>> subscribers = new ConcurrentHashMap<>();
    private final Supplier<ExecutorService> boxHandlerSupplier;

    private ExecutorService boxHandler;

    public DefaultPublisherPipelineReceiver() {
        this(null);
    }

    public DefaultPublisherPipelineReceiver(final Supplier<ExecutorService> boxHandlerSupplier) {
        this.boxHandlerSupplier = Objects.requireNonNullElse(boxHandlerSupplier, DEFAULT_BOX_HANDLER_SUPPLIER);
    }

    @Override
    public Result<Object> receive(final UUID sessionId, final PipelineBox<PublisherTask<T>> box) {
        log.info("The attempt of receiving : {} {}", sessionId, box);
        if (this.sessionId.get() == null){
            return DefaultResultBuilder.<Object>fail(Code.IS_BLOCKED.getValue());
        }

        if (!this.sessionId.get().equals(sessionId)) {
            return DefaultResultBuilder.<Object>fail(Code.INVALID_SESSION_ID.getValue());
        }

        if (subscribers.isEmpty()) {
            return DefaultResultBuilder.<Object>fail(Code.NO_ONE_SUBSCRIBER.getValue());
        }

        ArrayList<Seed> seeds = new ArrayList<>();
        for (Map.Entry<UUID, PipelineSubscriber<PublisherTask<T>>> entry : subscribers.entrySet()) {
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
            return DefaultResultBuilder.<Object>fail(Code.ALREADY_BLOCKED.getValue());
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
            boxHandler = boxHandlerSupplier.get();
            return DefaultResultBuilder.<Object>ok(null);
        }

        return DefaultResultBuilder.<Object>fail(Code.ALREADY_BLOCKED_OUT.getValue());
    }

    @Override
    public Result<PipelineSubscriber<PublisherTask<T>>> subscribe(final PipelineSubscriber<PublisherTask<T>> subscriber) {
        log.info("The attempt of subscription: {}", subscriber);
        if (subscribers.putIfAbsent(subscriber.getId(), subscriber) == null){
            log.info("Subscribed: {}", subscriber);
            subscriber.blockOut(sessionId.get());
            return DefaultResultBuilder.<PipelineSubscriber<PublisherTask<T>>>ok(subscriber);
        }

        log.info("Already subscribed: {}", subscriber);
        return DefaultResultBuilder.<PipelineSubscriber<PublisherTask<T>>>fail(Code.ALREADY_SUBSCRIBED.getValue());
    }

    @Override
    public Result<PipelineSubscriber<PublisherTask<T>>> unsubscribe(final PipelineSubscriber<PublisherTask<T>> subscriber) {
        log.info("The attempt of unsubscription: {}", subscriber);
        if (subscribers.remove(subscriber.getId()) != null) {
            log.info("Unsubscribed: {}", subscriber);
            subscriber.block();
            return DefaultResultBuilder.<PipelineSubscriber<PublisherTask<T>>>ok(subscriber);
        }

        log.info("Already unsubscribed: {}", subscriber);
        return DefaultResultBuilder.<PipelineSubscriber<PublisherTask<T>>>fail(Code.ALREADY_UNSUBSCRIBED.getValue());
    }
}
