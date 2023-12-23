package ru.multa.entia.conversion.impl.pipeline.subscriber;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.publisher.Publisher;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

import java.util.EnumMap;
import java.util.UUID;


public class DefaultPublisherPipelineSubscriber<T extends ConversationItem> extends AbstractPipelineSubscriber<T, PublisherTask<T>> {
    public static final EnumMap<Code, String> CODES = new EnumMap<>(Code.class) {{
        put(Code.SESSION_ID_IS_NOT_SET, "default-publisher-pipeline-subscriber.session-is-not-set");
        put(Code.SESSION_ID_ALREADY_RESET, "default-publisher-pipeline-subscriber.session-already-reset");
        put(Code.THIS_SESSION_ID_ALREADY_SET, "default-publisher-pipeline-subscriber.this-session-already-set");
        put(Code.DISALLOWED_SESSION_ID, "default-publisher-pipeline-subscriber.disallowed-session-id");
        put(Code.SESSION_ID_ON_BLOCK_OUT_IS_NULL, "default-publisher-pipeline-subscriber.session-id-on-block-out-is-null");
        put(Code.IS_BLOCKED, "default-publisher-pipeline-subscriber.is-blocked");
        put(Code.ALREADY_BLOCKED, "default-publisher-pipeline-subscriber.already-blocked");
        put(Code.ALREADY_BLOCKED_OUT, "default-publisher-pipeline-subscriber.already-blocked-out");
    }};

    private final Publisher<T> publisher;

    public DefaultPublisherPipelineSubscriber(final Publisher<T> publisher) {
        super(null, null);
        this.publisher = publisher;
    }

    public DefaultPublisherPipelineSubscriber(final Publisher<T> publisher, final UUID id, final UUID sessionId) {
        super(id, sessionId);
        this.publisher = publisher;
    }

    @Override
    public Result<PublisherTask<T>> give(final PublisherTask<T> task, final UUID sessionId) {
        return DefaultResultBuilder.<PublisherTask<T>>compute(
                () -> {return task;},
                () -> {
                    return DefaultSeedBuilder.<PublisherTask<T>>computeFromCodes(
                            () -> {return sessionId == null ? getCode(Code.DISALLOWED_SESSION_ID) : null;},
                            () -> {return this.getSessionId().get() == null ? getCode(Code.SESSION_ID_IS_NOT_SET) : null;}
                    );
                },
                () -> {
                    Result<T> result = publisher.publish(task);
                    return result.ok() ? null : result.seed();
                }
        );
    }

    @Override
    protected String getCode(final Code code) {
        return CODES.get(code);
    }
}
