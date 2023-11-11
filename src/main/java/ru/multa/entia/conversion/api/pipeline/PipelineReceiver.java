package ru.multa.entia.conversion.api.pipeline;

import ru.multa.entia.results.api.result.Result;

import java.util.UUID;

public interface PipelineReceiver<T> {
    Result<Object> receive(UUID sessionId, PipelineBox<T> box);
    Result<Object> block();
    Result<Object> blockOut(UUID sessionId);
    Result<PipelineSubscriber<T>> subscribe(PipelineSubscriber<T> subscriber);
    Result<PipelineSubscriber<T>> unsubscribe(PipelineSubscriber<T> subscriber);
}
