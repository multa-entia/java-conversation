package ru.multa.entia.conversion.impl.pipeline.subscriber;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.publisher.Publisher;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

import java.util.UUID;


public class DefaultPublisherPipelineSubscriber<T extends ConversationItem> extends AbstractPipelineSubscriber<T, PublisherTask<T>> {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(
                new CodeKey(DefaultPublisherPipelineSubscriber.class, Code.SESSION_ID_IS_NOT_SET),
                "pipeline.subscriber.publisher.default.session-is-not-set"
        );
        CR.update(
                new CodeKey(DefaultPublisherPipelineSubscriber.class, Code.SESSION_ID_ALREADY_RESET),
                "pipeline.subscriber.publisher.default.session-already-reset"
        );
        CR.update(
                new CodeKey(DefaultPublisherPipelineSubscriber.class, Code.THIS_SESSION_ID_ALREADY_SET),
                "pipeline.subscriber.publisher.default.this-session-already-set"
        );
        CR.update(
                new CodeKey(DefaultPublisherPipelineSubscriber.class, Code.DISALLOWED_SESSION_ID),
                "pipeline.subscriber.publisher.default.disallowed-session-id"
        );
        CR.update(
                new CodeKey(DefaultPublisherPipelineSubscriber.class, Code.SESSION_ID_ON_BLOCK_OUT_IS_NULL),
                "pipeline.subscriber.publisher.default.session-id-on-block-out-is-null"
        );
        CR.update(
                new CodeKey(DefaultPublisherPipelineSubscriber.class, Code.IS_BLOCKED),
                "pipeline.subscriber.publisher.default.is-blocked"
        );
        CR.update(
                new CodeKey(DefaultPublisherPipelineSubscriber.class, Code.ALREADY_BLOCKED),
                "pipeline.subscriber.publisher.default.already-blocked"
        );
        CR.update(
                new CodeKey(DefaultPublisherPipelineSubscriber.class, Code.ALREADY_BLOCKED_OUT),
                "pipeline.subscriber.publisher.default.already-blocked-out"
        );
    }

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
                            () -> {
                                return sessionId == null
                                        ? CR.get(new CodeKey(getClass(), Code.DISALLOWED_SESSION_ID))
                                        : null;
                                },
                            () -> {
                                return this.getSessionId().get() == null
                                        ? CR.get(new CodeKey(getClass(), Code.SESSION_ID_IS_NOT_SET))
                                        : null;
                            }
                    );
                },
                () -> {
                    Result<T> result = publisher.publish(task);
                    return result.ok() ? null : result.seed();
                }
        );
    }
}
