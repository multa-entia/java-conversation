package ru.multa.entia.conversion.impl.pipeline;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.pipeline.PipelineBoxHandlerTask;
import ru.multa.entia.conversion.api.pipeline.PipelineSubscriber;
import ru.multa.entia.conversion.api.publisher.PublisherTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

record DefaultPipelineBoxHandlerTask<T extends ConversationItem>(
        PipelineBox<PublisherTask<T>> box,
        Map<UUID, PipelineSubscriber<PublisherTask<T>>> actor,
        UUID sessionId,
        Lock actorLock)
        implements PipelineBoxHandlerTask<PublisherTask<T>> {
}
