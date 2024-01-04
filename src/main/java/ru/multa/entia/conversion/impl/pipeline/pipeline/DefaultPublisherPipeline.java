package ru.multa.entia.conversion.impl.pipeline.pipeline;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.pipeline.PipelineReceiver;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;

import java.util.concurrent.*;

public class DefaultPublisherPipeline<T extends ConversationItem> extends AbstractPipeline<T, PublisherTask<T>> {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(new CodeKey(DefaultPublisherPipeline.class, Code.ALREADY_STARTED), "pipeline.publisher.default.already-started");
        CR.update(new CodeKey(DefaultPublisherPipeline.class, Code.ALREADY_STOPPED), "pipeline.publisher.default.already-stopped");
        CR.update(new CodeKey(DefaultPublisherPipeline.class, Code.OFFER_IF_NOT_STARTED), "pipeline.publisher.default.offer-if-not-started");
        CR.update(new CodeKey(DefaultPublisherPipeline.class, Code.OFFER_QUEUE_IS_FULL), "pipeline.publisher.default.offer-queue-is-full");
    }

    public DefaultPublisherPipeline(final BlockingQueue<PipelineBox<PublisherTask<T>>> queue,
                                    final PipelineReceiver<PublisherTask<T>> receiver) {
        super(queue, receiver);
    }

    public DefaultPublisherPipeline(final BlockingQueue<PipelineBox<PublisherTask<T>>> queue,
                                    final PipelineReceiver<PublisherTask<T>> receiver,
                                    final ThreadParams threadParams) {
        super(queue, receiver, threadParams);
    }
}
