package ru.multa.entia.conversion.impl.pipeline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.pipeline.PipelineSubscriber;
import ru.multa.entia.conversion.api.publisher.Publisher;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultPublisherPipelineSubscriber<T extends ConversationItem> implements PipelineSubscriber<PublisherTask<T>> {
    @RequiredArgsConstructor
    @Getter
    public enum Code {
        SESSION_ID_IS_NOT_SET("default-publisher-pipeline-subscriber.session-is-not-set"),
        SESSION_ID_ALREADY_RESET("default-publisher-pipeline-subscriber.session-already-reset"),
        THIS_SESSION_ID_ALREADY_SET("default-publisher-pipeline-subscriber.this-session-already-set"),
        DISALLOWED_SESSION_ID("default-publisher-pipeline-subscriber.disallowed-session-id"),
        SESSION_ID_ON_BLOCK_OUT_IS_NULL("default-publisher-pipeline-subscriber.session-id-on-block-out-is-null"),

        IS_BLOCKED("default-publisher-pipeline-subscriber.is-blocked"),
        ALREADY_BLOCKED("default-publisher-pipeline-subscriber.already-blocked"),
        ALREADY_BLOCKED_OUT("default-publisher-pipeline-subscriber.already-blocked-out");

        private final String value;
    }

    @Getter
    private final UUID id;
    private final Publisher<T> publisher;
    private final AtomicReference<UUID> sessionId;

    public DefaultPublisherPipelineSubscriber(final Publisher<T> publisher) {
        this(publisher, null, null);
    }

    public DefaultPublisherPipelineSubscriber(final Publisher<T> publisher, final UUID id, final UUID sessionId) {
        this.publisher = publisher;
        this.id = Objects.requireNonNullElseGet(id, UUID::randomUUID);
        this.sessionId = new AtomicReference<>(sessionId);
    }

    @Override
    public Result<PublisherTask<T>> give(final PublisherTask<T> task, final UUID sessionId) {
        return DefaultResultBuilder.<PublisherTask<T>>compute(
                () -> {return task;},
                () -> {
                    return DefaultSeedBuilder.<PublisherTask<T>>computeFromCodes(
                            () -> {return sessionId == null ? Code.DISALLOWED_SESSION_ID.getValue() : null;},
                            () -> {return this.sessionId.get() == null ? Code.SESSION_ID_IS_NOT_SET.getValue() : null;}
                    );
                },
                () -> {
                    Result<T> result = publisher.publish(task);
                    return result.ok() ? null : result.seed();
                }
        );
    }

    @Override
    public Result<Object> block() {
        return DefaultResultBuilder.<Object>computeFromCodes(
                () -> {return null;},
                () -> {
                    UUID previous = sessionId.getAndSet(null);
                    return previous == null ? Code.SESSION_ID_ALREADY_RESET.getValue() : null;
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
