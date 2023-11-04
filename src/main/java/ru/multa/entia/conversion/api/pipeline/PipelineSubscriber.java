package ru.multa.entia.conversion.api.pipeline;

import ru.multa.entia.results.api.result.Result;

import java.util.UUID;

public interface PipelineSubscriber<T> {
    UUID getId();
    Result<T> give(T value);
}
