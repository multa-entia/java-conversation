package ru.multa.entia.conversion.api.pipeline;

import ru.multa.entia.results.api.result.Result;

public interface Pipeline<T> {
    Result<Object> start();
    Result<Object> stop(boolean clear);
    Result<T> offer(PipelineBox<T> box);
}
