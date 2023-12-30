package ru.multa.entia.conversion.impl.pipeline.pipeline;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.pipeline.PipelineReceiver;
import ru.multa.entia.conversion.api.publisher.PublisherTask;

import java.util.EnumMap;
import java.util.concurrent.*;

public class DefaultPublisherPipeline<T extends ConversationItem> extends AbstractPipeline<T, PublisherTask<T>> {
    public static final EnumMap<Code, String> CODES = new EnumMap<>(Code.class){{
        put(Code.ALREADY_STARTED, "default-publisher-pipeline.already-started");
        put(Code.ALREADY_STOPPED, "default-publisher-pipeline.already-stopped");
        put(Code.OFFER_IF_NOT_STARTED, "default-publisher-pipeline.offer-if-not-started");
        put(Code.OFFER_QUEUE_IS_FULL, "default-publisher-pipeline.offer-queue-is-gull");
    }};

    public DefaultPublisherPipeline(final BlockingQueue<PipelineBox<PublisherTask<T>>> queue,
                                    final PipelineReceiver<PublisherTask<T>> receiver) {
        super(queue, receiver);
    }

    public DefaultPublisherPipeline(final BlockingQueue<PipelineBox<PublisherTask<T>>> queue,
                                    final PipelineReceiver<PublisherTask<T>> receiver,
                                    final ThreadParams threadParams) {
        super(queue, receiver, threadParams);
    }

    @Override
    protected String getCode(final Code code) {
        return CODES.get(code);
    }
}
