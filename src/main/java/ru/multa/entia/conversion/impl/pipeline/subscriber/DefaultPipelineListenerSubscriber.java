package ru.multa.entia.conversion.impl.pipeline.subscriber;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.listener.Listener;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

import java.util.EnumMap;
import java.util.UUID;

public class DefaultPipelineListenerSubscriber<T extends ConversationItem> extends AbstractPipelineSubscriber<T, ListenerTask<T>> {
    public static final EnumMap<Code, String> CODES = new EnumMap<>(Code.class) {{
        put(Code.SESSION_ID_IS_NOT_SET, "default-listener-pipeline-subscriber.session-is-not-set");
        put(Code.SESSION_ID_ALREADY_RESET, "default-listener-pipeline-subscriber.session-already-reset");
        put(Code.THIS_SESSION_ID_ALREADY_SET, "default-listener-pipeline-subscriber.this-session-already-set");
        put(Code.DISALLOWED_SESSION_ID, "default-listener-pipeline-subscriber.disallowed-session-id");
        put(Code.SESSION_ID_ON_BLOCK_OUT_IS_NULL, "default-listener-pipeline-subscriber.session-id-on-block-out-is-null");
        put(Code.IS_BLOCKED, "default-listener-pipeline-subscriber.is-blocked");
        put(Code.ALREADY_BLOCKED, "default-listener-pipeline-subscriber.already-blocked");
        put(Code.ALREADY_BLOCKED_OUT, "default-listener-pipeline-subscriber.already-blocked-out");
    }};

    private final Listener<T> listener;

    public DefaultPipelineListenerSubscriber(final Listener<T> listener) {
        super(null, null);
        this.listener = listener;
    }

    public DefaultPipelineListenerSubscriber(final Listener<T> listener, final UUID id, final UUID sessionId) {
        super(id, sessionId);
        this.listener = listener;
    }

    @Override
    public Result<ListenerTask<T>> give(final ListenerTask<T> task, final UUID sessionId) {
        return DefaultResultBuilder.<ListenerTask<T>>compute(
                () -> {return task;},
                () -> {
                    return DefaultSeedBuilder.<ListenerTask<T>>computeFromCodes(
                            () -> {return sessionId == null ? getCode(Code.DISALLOWED_SESSION_ID) : null;},
                            () -> {return this.getSessionId().get() == null ? getCode(Code.SESSION_ID_IS_NOT_SET) : null;}
                    );
                },
                () -> {
                    Result<T> result = listener.listen(task);
                    return result.ok() ? null : result.seed();
                }
        );
    }

    @Override
    protected String getCode(Code code) {
        return CODES.get(code);
    }
}