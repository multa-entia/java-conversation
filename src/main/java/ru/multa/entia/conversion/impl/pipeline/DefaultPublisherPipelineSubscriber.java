package ru.multa.entia.conversion.impl.pipeline;

import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.pipeline.PipelineSubscriber;
import ru.multa.entia.conversion.api.publisher.Publisher;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

@RequiredArgsConstructor
public class DefaultPublisherPipelineSubscriber<T extends ConversationItem> implements PipelineSubscriber<PublisherTask<T>> {
    private final Publisher<T> publisher;

    @Override
    public Result<PublisherTask<T>> give(final PublisherTask<T> task) {
        Result<T> result = publisher.publish(task);
        return result.ok()
                ? DefaultResultBuilder.<PublisherTask<T>>ok(task)
                : DefaultResultBuilder.<PublisherTask<T>>fail(result.seed());
    }
}
