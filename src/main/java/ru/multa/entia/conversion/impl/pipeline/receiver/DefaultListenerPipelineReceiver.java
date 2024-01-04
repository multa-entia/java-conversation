package ru.multa.entia.conversion.impl.pipeline.receiver;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;

public class DefaultListenerPipelineReceiver<T extends ConversationItem> extends AbstractPipelineReceiver<T, ListenerTask<T>> {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(
                new CodeKey(DefaultListenerPipelineReceiver.class, Code.ALREADY_BLOCKED_OUT),
                "pipeline.receiver.listener.default.already-blocked-out"
        );
        CR.update(
                new CodeKey(DefaultListenerPipelineReceiver.class, Code.ALREADY_BLOCKED),
                "pipeline.receiver.listener.default.already-blocked"
        );
        CR.update(
                new CodeKey(DefaultListenerPipelineReceiver.class, Code.ALREADY_SUBSCRIBED),
                "pipeline.receiver.listener.default.already-subscribed"
        );
        CR.update(
                new CodeKey(DefaultListenerPipelineReceiver.class, Code.ALREADY_UNSUBSCRIBED),
                "pipeline.receiver.listener.default.already-unsubscribed"
        );
        CR.update(
                new CodeKey(DefaultListenerPipelineReceiver.class, Code.IS_BLOCKED),
                "pipeline.receiver.listener.default.is-blocked"
        );
        CR.update(
                new CodeKey(DefaultListenerPipelineReceiver.class, Code.INVALID_SESSION_ID),
                "pipeline.receiver.listener.default.invalid-session-id"
        );
        CR.update(
                new CodeKey(DefaultListenerPipelineReceiver.class, Code.NO_ONE_SUBSCRIBER),
                "pipeline.receiver.listener.default.no-one-subscriber"
        );
        CR.update(
                new CodeKey(DefaultListenerPipelineReceiver.class, Code.SUBSCRIBER_FAIL),
                "pipeline.receiver.listener.default.subscriber-fail"
        );
    }
}
