package ru.multa.entia.conversion.api.pipeline;

import ru.multa.entia.results.api.result.Result;

public interface Pipeline<T> {
    // TODO: 08.11.2023 del
//    Result<PipelineSubscriber<T>> subscribe(PipelineSubscriber<T> subscriber);
//    Result<PipelineSubscriber<T>> unsubscribe(PipelineSubscriber<T> subscriber);
    Result<Object> start();
    Result<Object> stop(boolean clear);
    Result<T> offer(PipelineBox<T> box);
    // TODO: 08.11.2023 ???
//    Result<Object> stopWithClearing();
}
