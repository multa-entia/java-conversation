package ru.multa.entia.conversion.api.pipeline;

import ru.multa.entia.results.api.result.Result;

public interface Pipeline<T> {
    Result<PipelineSubscriber<T>> subscribe(PipelineSubscriber<T> subscriber);
    Result<PipelineSubscriber<T>> unsubscribe(PipelineSubscriber<T> subscriber);
    Result<T> offer(PipelineBox<T> box);
    Result<Object> start();
    Result<Object> stop();
    Result<Object> stopWithoutClearing();
}
