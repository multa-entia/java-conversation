package ru.multa.entia.conversion.impl.pipeline;

import ru.multa.entia.conversion.api.pipeline.PipelineBox;

public record DefaultPipelineBox<T>(T value) implements PipelineBox<T> {}
