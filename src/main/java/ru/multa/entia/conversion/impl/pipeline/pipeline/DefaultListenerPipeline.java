package ru.multa.entia.conversion.impl.pipeline.pipeline;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.pipeline.PipelineReceiver;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;

import java.util.concurrent.BlockingQueue;

public class DefaultListenerPipeline<T extends ConversationItem> extends AbstractPipeline<T, ListenerTask<T>> {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(new CodeKey(DefaultListenerPipeline.class, Code.ALREADY_STARTED), "pipeline.listener.default.already-started");
        CR.update(new CodeKey(DefaultListenerPipeline.class, Code.ALREADY_STOPPED), "pipeline.listener.default.already-stopped");
        CR.update(new CodeKey(DefaultListenerPipeline.class, Code.OFFER_IF_NOT_STARTED), "pipeline.listener.default.offer-if-not-started");
        CR.update(new CodeKey(DefaultListenerPipeline.class, Code.OFFER_QUEUE_IS_FULL), "pipeline.listener.default.offer-queue-is-full");
    }


    public DefaultListenerPipeline(final BlockingQueue<PipelineBox<ListenerTask<T>>> queue,
                                   final PipelineReceiver<ListenerTask<T>> receiver) {
        super(queue, receiver);
    }

    public DefaultListenerPipeline(final BlockingQueue<PipelineBox<ListenerTask<T>>> queue,
                                   final PipelineReceiver<ListenerTask<T>> receiver,
                                   final ThreadParams threadParams) {
        super(queue, receiver, threadParams);
    }
}
