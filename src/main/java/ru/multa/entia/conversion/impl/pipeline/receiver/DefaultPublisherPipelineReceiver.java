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
                "conversation:pipeline.receiver.publisher.default:already-blocked-out"
        );
        CR.update(
                new CodeKey(DefaultPublisherPipelineReceiver.class, Code.ALREADY_BLOCKED),
                "conversation:pipeline.receiver.publisher.default:already-blocked"
        );
        CR.update(
                new CodeKey(DefaultPublisherPipelineReceiver.class, Code.ALREADY_SUBSCRIBED),
                "conversation:pipeline.receiver.publisher.default:already-subscribed"
        );
        CR.update(
                new CodeKey(DefaultPublisherPipelineReceiver.class, Code.ALREADY_UNSUBSCRIBED),
                "conversation:pipeline.receiver.publisher.default:already-unsubscribed"
        );
        CR.update(
                new CodeKey(DefaultPublisherPipelineReceiver.class, Code.IS_BLOCKED),
                "conversation:pipeline.receiver.publisher.default:is-blocked"
        );
        CR.update(
                new CodeKey(DefaultPublisherPipelineReceiver.class, Code.INVALID_SESSION_ID),
                "conversation:pipeline.receiver.publisher.default:invalid-session-id"
        );
        CR.update(
                new CodeKey(DefaultPublisherPipelineReceiver.class, Code.NO_ONE_SUBSCRIBER),
                "conversation:pipeline.receiver.publisher.default:no-one-subscriber"
        );
        CR.update(
                new CodeKey(DefaultPublisherPipelineReceiver.class, Code.SUBSCRIBER_FAIL),
                "conversation:pipeline.receiver.publisher.default:subscriber-fail"
        );
    }
}