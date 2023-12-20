package ru.multa.entia.conversion.impl.pipeline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.pipeline.PipelineReceiver;
import ru.multa.entia.conversion.api.pipeline.PipelineSubscriber;
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

@Slf4j
public class DefaultPipelineListenerReceiver<T extends ConversationItem> implements PipelineReceiver<ListenerTask<T>> {
    @RequiredArgsConstructor
    @Getter
    public enum Code {
        ALREADY_BLOCKED_OUT("default-listener-receiver.already-blocked-out"),
        ALREADY_BLOCKED("default-listener-receiver.already-blocked"),
        ALREADY_SUBSCRIBED("default-listener-receiver.already-subscribed"),
        ALREADY_UNSUBSCRIBED("default-listener-receiver.already-unsubscribed"),
        IS_BLOCKED("default-listener-receiver.is-blocked"),
        INVALID_SESSION_ID("default-listener-receiver.invalid-session-id"),
        NO_ONE_SUBSCRIBER("default-listener-receiver.no-one-subscriber"),
        SUBSCRIBER_FAIL("default-listener-receiver.subscriber-fail");

        private final String value;
    }

    private static final String DEFAULT_BOX_HANDLER_THREAD_PREFIX = "listener-box-handler-thread-";
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
    private final ConcurrentMap<UUID, PipelineSubscriber<ListenerTask<T>>> subscribers = new ConcurrentHashMap<>();
    private final Supplier<ExecutorService> boxHandlerSupplier;

    private ExecutorService boxHandler;

    public DefaultPipelineListenerReceiver() {
        this(null);
    }

    public DefaultPipelineListenerReceiver(final Supplier<ExecutorService> boxHandlerSupplier) {
        this.boxHandlerSupplier = Objects.requireNonNullElse(boxHandlerSupplier, DEFAULT_BOX_HANDLER_SUPPLIER);
    }

    @Override
    public Result<Object> receive(final UUID sessionId, final PipelineBox<ListenerTask<T>> box) {
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
        for (Map.Entry<UUID, PipelineSubscriber<ListenerTask<T>>> entry : subscribers.entrySet()) {
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
    public Result<PipelineSubscriber<ListenerTask<T>>> subscribe(final PipelineSubscriber<ListenerTask<T>> subscriber) {
        log.info("The attempt of subscription: {}", subscriber);
        if (subscribers.putIfAbsent(subscriber.getId(), subscriber) == null){
            log.info("Subscribed: {}", subscriber);
            // TODO: 20.12.2023 !!! test
            subscriber.blockOut(sessionId.get());
            return DefaultResultBuilder.<PipelineSubscriber<ListenerTask<T>>>ok(subscriber);
        }

        log.info("Already subscribed: {}", subscriber);
        return DefaultResultBuilder.<PipelineSubscriber<ListenerTask<T>>>fail(Code.ALREADY_SUBSCRIBED.getValue());
    }

    @Override
    public Result<PipelineSubscriber<ListenerTask<T>>> unsubscribe(final PipelineSubscriber<ListenerTask<T>> subscriber) {
        log.info("The attempt of unsubscription: {}", subscriber);
        if (subscribers.remove(subscriber.getId()) != null) {
            log.info("Unsubscribed: {}", subscriber);
            // TODO: 20.12.2023 !!! test
            subscriber.block();
            return DefaultResultBuilder.<PipelineSubscriber<ListenerTask<T>>>ok(subscriber);
        }

        log.info("Already unsubscribed: {}", subscriber);
        return DefaultResultBuilder.<PipelineSubscriber<ListenerTask<T>>>fail(Code.ALREADY_UNSUBSCRIBED.getValue());
    }
}
