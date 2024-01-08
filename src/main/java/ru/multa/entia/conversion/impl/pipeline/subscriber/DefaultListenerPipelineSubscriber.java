package ru.multa.entia.conversion.impl.pipeline.subscriber;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.listener.Listener;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

import java.util.UUID;

public class DefaultListenerPipelineSubscriber<T extends ConversationItem> extends AbstractPipelineSubscriber<T, ListenerTask<T>> {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(
                new CodeKey(DefaultListenerPipelineSubscriber.class, Code.SESSION_ID_IS_NOT_SET),
                "conversation:pipeline.subscriber.listener.default:session-is-not-set"
        );
        CR.update(
                new CodeKey(DefaultListenerPipelineSubscriber.class, Code.SESSION_ID_ALREADY_RESET),
                "conversation:pipeline.subscriber.listener.default:session-already-reset"
        );
        CR.update(
                new CodeKey(DefaultListenerPipelineSubscriber.class, Code.THIS_SESSION_ID_ALREADY_SET),
                "conversation:pipeline.subscriber.listener.default:this-session-already-set"
        );
        CR.update(
                new CodeKey(DefaultListenerPipelineSubscriber.class, Code.DISALLOWED_SESSION_ID),
                "conversation:pipeline.subscriber.listener.default:disallowed-session-id"
        );
        CR.update(
                new CodeKey(DefaultListenerPipelineSubscriber.class, Code.SESSION_ID_ON_BLOCK_OUT_IS_NULL),
                "conversation:pipeline.subscriber.listener.default:session-id-on-block-out-is-null"
        );
        CR.update(
                new CodeKey(DefaultListenerPipelineSubscriber.class, Code.IS_BLOCKED),
                "conversation:pipeline.subscriber.listener:default.is-blocked"
        );
        CR.update(
                new CodeKey(DefaultListenerPipelineSubscriber.class, Code.ALREADY_BLOCKED),
                "conversation:pipeline.subscriber.listener.default:already-blocked"
        );
        CR.update(
                new CodeKey(DefaultListenerPipelineSubscriber.class, Code.ALREADY_BLOCKED_OUT),
                "conversation:pipeline.subscriber.listener.default:already-blocked-out"
        );
    }

    private final Listener<T> listener;

    public DefaultListenerPipelineSubscriber(final Listener<T> listener) {
        super(null, null);
        this.listener = listener;
    }

    public DefaultListenerPipelineSubscriber(final Listener<T> listener, final UUID id, final UUID sessionId) {
        super(id, sessionId);
        this.listener = listener;
    }

    @Override
    public Result<ListenerTask<T>> give(final ListenerTask<T> task, final UUID sessionId) {
        return DefaultResultBuilder.<ListenerTask<T>>compute(
                () -> {return task;},
                () -> {
                    return DefaultSeedBuilder.<ListenerTask<T>>computeFromCodes(
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
                    Result<T> result = listener.listen(task);
                    return result.ok() ? null : result.seed();
                }
        );
    }
}