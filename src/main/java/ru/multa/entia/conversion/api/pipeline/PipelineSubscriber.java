package ru.multa.entia.conversion.api.pipeline;

import ru.multa.entia.conversion.api.block.Blocking;
import ru.multa.entia.results.api.result.Result;

import java.util.UUID;

public interface PipelineSubscriber<T> extends Blocking<UUID, Object> {
    UUID getId();
    Result<T> give(T value, UUID sessionId);
}
