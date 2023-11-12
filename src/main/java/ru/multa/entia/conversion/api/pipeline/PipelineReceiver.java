package ru.multa.entia.conversion.api.pipeline;

import ru.multa.entia.results.api.result.Result;

import java.util.UUID;

public interface PipelineReceiver<T> {
    Result<Object> receive(UUID sessionId, PipelineBox<T> box);

    // TODO: 12.11.2023 del
    Result<Object> block();  // TODO: 12.11.2023 move to sep interface
    Result<Object> blockOut(UUID sessionId);  // TODO: 12.11.2023 move to sep interface
    Result<PipelineSubscriber<T>> subscribe(PipelineSubscriber<T> subscriber);  // TODO: 12.11.2023 move to sep interface
    Result<PipelineSubscriber<T>> unsubscribe(PipelineSubscriber<T> subscriber);  // TODO: 12.11.2023 move to sep interface
}
