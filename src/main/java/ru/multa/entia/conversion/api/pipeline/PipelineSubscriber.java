package ru.multa.entia.conversion.api.pipeline;

import ru.multa.entia.results.api.result.Result;

public interface PipelineSubscriber<T> {
    Result<T> give(T value);
}
