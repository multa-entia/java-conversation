package ru.multa.entia.conversion.api.pipeline;

import ru.multa.entia.results.api.result.Result;

public interface PipelineBoxHandler<T> {
    Result<Object> handle(PipelineBoxHandlerTask<T> task);
}
