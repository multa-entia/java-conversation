package ru.multa.entia.conversion.impl.pipeline.receiver;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;


public class DefaultPublisherPipelineReceiver<T extends ConversationItem> extends AbstractPipelineReceiver<T, PublisherTask<T>> {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(
                new CodeKey(DefaultPublisherPipelineReceiver.class, Code.ALREADY_BLOCKED_OUT),
                "pipeline.receiver.publisher.default.already-blocked-out"
        );
        CR.update(
                new CodeKey(DefaultPublisherPipelineReceiver.class, Code.ALREADY_BLOCKED),
                "pipeline.receiver.publisher.default.already-blocked"
        );
        CR.update(
                new CodeKey(DefaultPublisherPipelineReceiver.class, Code.ALREADY_SUBSCRIBED),
                "pipeline.receiver.publisher.default.already-subscribed"
        );
        CR.update(
                new CodeKey(DefaultPublisherPipelineReceiver.class, Code.ALREADY_UNSUBSCRIBED),
                "pipeline.receiver.publisher.default.already-unsubscribed"
        );
        CR.update(
                new CodeKey(DefaultPublisherPipelineReceiver.class, Code.IS_BLOCKED),
                "pipeline.receiver.publisher.default.is-blocked"
        );
        CR.update(
                new CodeKey(DefaultPublisherPipelineReceiver.class, Code.INVALID_SESSION_ID),
                "pipeline.receiver.publisher.default.invalid-session-id"
        );
        CR.update(
                new CodeKey(DefaultPublisherPipelineReceiver.class, Code.NO_ONE_SUBSCRIBER),
                "pipeline.receiver.publisher.default.no-one-subscriber"
        );
        CR.update(
                new CodeKey(DefaultPublisherPipelineReceiver.class, Code.SUBSCRIBER_FAIL),
                "pipeline.receiver.publisher.default.subscriber-fail"
        );
    }
}