package ru.multa.entia.conversion.impl.pipeline.subscriber;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.listener.Listener;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.conversion.api.pipeline.PipelineSubscriber;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

// TODO: 23.12.2023 !!!
//public class AbstractPipelineSubscriberImpl<T extends ConversationItem> extends AbstractPipelineSubscriber<T, ListenerTask<T>> {

public class DefaultPipelineListenerSubscriber<T extends ConversationItem> implements PipelineSubscriber<ListenerTask<T>> {
    @RequiredArgsConstructor
    @Getter
    public enum Code {
        SESSION_ID_IS_NOT_SET("default-listener-pipeline-subscriber.session-is-not-set"),
        SESSION_ID_ALREADY_RESET("default-listener-pipeline-subscriber.session-already-reset"),
        THIS_SESSION_ID_ALREADY_SET("default-listener-pipeline-subscriber.this-session-already-set"),
        DISALLOWED_SESSION_ID("default-listener-pipeline-subscriber.disallowed-session-id"),
        SESSION_ID_ON_BLOCK_OUT_IS_NULL("default-listener-pipeline-subscriber.session-id-on-block-out-is-null"),

        IS_BLOCKED("default-listener-pipeline-subscriber.is-blocked"),
        ALREADY_BLOCKED("default-listener-pipeline-subscriber.already-blocked"),
        ALREADY_BLOCKED_OUT("default-listener-pipeline-subscriber.already-blocked-out");

        private final String value;
    }

    @Getter
    private final UUID id;
    private final Listener<T> listener;
    private final AtomicReference<UUID> sessionId;

    public DefaultPipelineListenerSubscriber(Listener<T> listener) {
        this(listener, null, null);
    }

    public DefaultPipelineListenerSubscriber(final Listener<T> listener, final UUID id, final UUID sessionId) {
        this.listener = listener;
        this.id = Objects.requireNonNullElse(id, UUID.randomUUID());
        this.sessionId = new AtomicReference<>(sessionId);
    }

    @Override
    public Result<ListenerTask<T>> give(final ListenerTask<T> task, final UUID sessionId) {
        return DefaultResultBuilder.<ListenerTask<T>>compute(
                () -> {return task;},
                () -> {
                    return DefaultSeedBuilder.<ListenerTask<T>>computeFromCodes(
                            () -> {return sessionId == null ? Code.DISALLOWED_SESSION_ID.getValue() : null;},
                            () -> {return this.sessionId.get() == null ? Code.SESSION_ID_IS_NOT_SET.getValue() : null;}
                    );
                },
                () -> {
                    Result<T> result = listener.listen(task);
                    return result.ok() ? null : result.seed();
                }
        );
    }

    @Override
    public Result<Object> block() {
        return DefaultResultBuilder.<Object>computeFromCodes(
                () -> {return null;},
                () -> {
                    return sessionId.getAndSet(null) == null
                            ? Code.SESSION_ID_ALREADY_RESET.getValue()
                            : null;
                }
        );
    }

    @Override
    public Result<Object> blockOut(final UUID sessionId) {
        return DefaultResultBuilder.<Object>computeFromCodes(
                () -> {return null;},
                () -> {return sessionId == null ? Code.SESSION_ID_ON_BLOCK_OUT_IS_NULL.getValue() : null;},
                () -> {
                    UUID previous = this.sessionId.getAndSet(sessionId);
                    return previous == sessionId ? Code.THIS_SESSION_ID_ALREADY_SET.getValue() : null;
                }
        );
    }
}
