package ru.multa.entia.conversion.impl.pipeline;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.pipeline.PipelineReceiver;
import ru.multa.entia.conversion.api.publisher.Publisher;
import ru.multa.entia.results.api.result.Result;

import java.util.UUID;

public class DefaultPipelineReceiver<T extends ConversationItem> implements PipelineReceiver<Publisher<T>> {

    @Override
    public Result<Object> receive(UUID sessionId, PipelineBox<Publisher<T>> box) {
        return null;
    }

    @Override
    public Result<Object> block() {
        return null;
    }

    @Override
    public Result<Object> blockOut(UUID sessionId) {
        return null;
    }
}
