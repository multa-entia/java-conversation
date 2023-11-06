package ru.multa.entia.conversion.impl.pipeline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.pipeline.PipelineSubscriber;
import ru.multa.entia.conversion.api.publisher.Publisher;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

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
        Code code = null;
        if (sessionId == null) {
            code = Code.DISALLOWED_SESSION_ID;
        } else if (this.sessionId.get() == null) {
            code = Code.SESSION_ID_IS_NOT_SET;
        }

        if (code != null) {
            return DefaultResultBuilder.<PublisherTask<T>>fail(code.getValue());
        }

        Result<T> result = publisher.publish(task);
        return result.ok()
                ? DefaultResultBuilder.<PublisherTask<T>>ok(task)
                : DefaultResultBuilder.<PublisherTask<T>>fail(result.seed());
    }

    @Override
    public Result<Object> block() {
        UUID previous = sessionId.getAndSet(null);
        return previous == null
                ? DefaultResultBuilder.<Object>fail(Code.SESSION_ID_ALREADY_RESET.getValue())
                : DefaultResultBuilder.<Object>ok(null);
    }

    @Override
    public Result<Object> blockOut(final UUID sessionId) {
        Code code = Code.SESSION_ID_ON_BLOCK_OUT_IS_NULL;
        if (sessionId != null){
            UUID previous = this.sessionId.getAndSet(sessionId);
            code = previous == sessionId ? Code.THIS_SESSION_ID_ALREADY_SET : null;
        }

        return code == null
                ? DefaultResultBuilder.<Object>ok(null)
                : DefaultResultBuilder.<Object>fail(code.getValue());
    }
}
