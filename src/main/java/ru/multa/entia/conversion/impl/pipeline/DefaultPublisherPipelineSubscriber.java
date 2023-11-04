package ru.multa.entia.conversion.impl.pipeline;

import lombok.Getter;
import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.pipeline.PipelineSubscriber;
import ru.multa.entia.conversion.api.publisher.Publisher;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.UUID;

public class DefaultPublisherPipelineSubscriber<T extends ConversationItem> implements PipelineSubscriber<PublisherTask<T>> {
    @Getter
    private final UUID id;
    private final Publisher<T> publisher;

    public DefaultPublisherPipelineSubscriber(final Publisher<T> publisher) {
        this(null, publisher);
    }

    public DefaultPublisherPipelineSubscriber(final UUID id, final Publisher<T> publisher) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.publisher = publisher;
    }

    @Override
    public Result<PublisherTask<T>> give(final PublisherTask<T> task) {
        Result<T> result = publisher.publish(task);
        return result.ok()
                ? DefaultResultBuilder.<PublisherTask<T>>ok(task)
                : DefaultResultBuilder.<PublisherTask<T>>fail(result.seed());
    }
}
