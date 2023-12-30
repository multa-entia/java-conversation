package ru.multa.entia.conversion.impl.pipeline.pipeline;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.pipeline.PipelineReceiver;

import java.util.EnumMap;
import java.util.concurrent.BlockingQueue;

public class DefaultListenerPipeline<T extends ConversationItem> extends AbstractPipeline<T, ListenerTask<T>> {
    public static final EnumMap<Code, String> CODES = new EnumMap<>(Code.class){{
        put(Code.ALREADY_STARTED, "default-listener-pipeline.already-started");
        put(Code.ALREADY_STOPPED, "default-listener-pipeline.already-stopped");
        put(Code.OFFER_IF_NOT_STARTED, "default-listener-pipeline.offer-if-not-started");
        put(Code.OFFER_QUEUE_IS_FULL, "default-listener-pipeline.offer-queue-is-gull");
    }};

    public DefaultListenerPipeline(final BlockingQueue<PipelineBox<ListenerTask<T>>> queue,
                                   final PipelineReceiver<ListenerTask<T>> receiver) {
        super(queue, receiver);
    }

    public DefaultListenerPipeline(final BlockingQueue<PipelineBox<ListenerTask<T>>> queue,
                                   final PipelineReceiver<ListenerTask<T>> receiver,
                                   final ThreadParams threadParams) {
        super(queue, receiver, threadParams);
    }

    @Override
    protected String getCode(final Code code) {
        return CODES.get(code);
    }
}
