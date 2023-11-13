package ru.multa.entia.conversion.api.pipeline;

import ru.multa.entia.conversion.api.block.Blocking;
import ru.multa.entia.conversion.api.subscription.Subscription;
import ru.multa.entia.results.api.result.Result;

import java.util.UUID;

public interface PipelineReceiver<T> extends Blocking<UUID, Object>, Subscription<PipelineSubscriber<T>> {
    Result<Object> receive(UUID sessionId, PipelineBox<T> box);
}
