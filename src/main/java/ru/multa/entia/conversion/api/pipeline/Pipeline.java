package ru.multa.entia.conversion.api.pipeline;

import ru.multa.entia.results.api.result.Result;

public interface Pipeline<T> {
    Result<T> offer(T box);
    Result<PipelineSubscriber<T>> subscribe(PipelineSubscriber<T> subscriber);
    Result<PipelineSubscriber<T>> unsubscribe(PipelineSubscriber<T> subscriber);
}
