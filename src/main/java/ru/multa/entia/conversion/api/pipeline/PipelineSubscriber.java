package ru.multa.entia.conversion.api.pipeline;

import ru.multa.entia.results.api.result.Result;

import java.util.UUID;

public interface PipelineSubscriber<T> {
    UUID getId();
    Result<T> give(T value, UUID sessionId);
    Result<Object> block(); // TODO: 12.11.2023 move to sep interface
    Result<Object> blockOut(UUID sessionId); // TODO: 12.11.2023 move to sep interface
}
