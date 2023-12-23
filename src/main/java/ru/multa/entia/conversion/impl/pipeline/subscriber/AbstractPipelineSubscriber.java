package ru.multa.entia.conversion.impl.pipeline.subscriber;

import lombok.Getter;
import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.pipeline.PipelineSubscriber;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

abstract public class AbstractPipelineSubscriber<T extends ConversationItem, TASK> implements PipelineSubscriber<TASK> {
    public enum Code {
        SESSION_ID_IS_NOT_SET,
        SESSION_ID_ALREADY_RESET,
        THIS_SESSION_ID_ALREADY_SET,
        DISALLOWED_SESSION_ID,
        SESSION_ID_ON_BLOCK_OUT_IS_NULL,
        IS_BLOCKED,
        ALREADY_BLOCKED,
        ALREADY_BLOCKED_OUT;
    }

    @Getter
    private final UUID id;
    private final AtomicReference<UUID> sessionId;

    public AbstractPipelineSubscriber(final UUID id, final UUID sessionId) {
        this.id = Objects.requireNonNullElse(id, UUID.randomUUID());
        this.sessionId = new AtomicReference<>(sessionId);
    }

    @Override
    public Result<Object> blockOut(final UUID sessionId) {
        return DefaultResultBuilder.<Object>computeFromCodes(
                () -> {return null;},
                () -> {return sessionId == null ? getCode(Code.SESSION_ID_ON_BLOCK_OUT_IS_NULL) : null;},
                () -> {
                    UUID previous = this.sessionId.getAndSet(sessionId);
                    return previous == sessionId ? getCode(Code.THIS_SESSION_ID_ALREADY_SET) : null;
                }
        );
    }

    @Override
    public Result<Object> block() {
        return DefaultResultBuilder.<Object>computeFromCodes(
                () -> {return null;},
                () -> {
                    return sessionId.getAndSet(null) == null
                            ? getCode(Code.SESSION_ID_ALREADY_RESET)
                            : null;
                }
        );
    }

    protected AtomicReference<UUID> getSessionId() {
        return this.sessionId;
    }

    protected abstract String getCode(Code code);
}
