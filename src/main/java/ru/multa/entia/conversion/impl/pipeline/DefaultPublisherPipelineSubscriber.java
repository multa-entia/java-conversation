package ru.multa.entia.conversion.impl.pipeline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.pipeline.PipelineSubscriber;
import ru.multa.entia.conversion.api.publisher.Publisher;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultPublisherPipelineSubscriber<T extends ConversationItem> implements PipelineSubscriber<PublisherTask<T>> {
    @RequiredArgsConstructor
    @Getter
    public enum Code {
        IS_BLOCKED("default-publisher-pipeline-subscriber.is-blocked"),
        ALREADY_BLOCKED("default-publisher-pipeline-subscriber.already-blocked"),
        ALREADY_BLOCKED_OUT("default-publisher-pipeline-subscriber.already-blocked-out");

        private final String value;
    }

    private static final boolean DEFAULT_BLOCK_STATE = true;

    @Getter
    private final UUID id;
    private final Publisher<T> publisher;
    private final AtomicBoolean block;

    public DefaultPublisherPipelineSubscriber(final Publisher<T> publisher) {
        this(publisher, null, null);
    }

    public DefaultPublisherPipelineSubscriber(final Publisher<T> publisher, final UUID id, final AtomicBoolean block) {
        this.publisher = publisher;
        this.id = Objects.requireNonNullElseGet(id, UUID::randomUUID);
        this.block = Objects.requireNonNullElseGet(block, () -> new AtomicBoolean(DEFAULT_BLOCK_STATE));
    }

    @Override
    public Result<PublisherTask<T>> give(final PublisherTask<T> task) {
        if (!block.get()){
            Result<T> result = publisher.publish(task);
            return result.ok()
                    ? DefaultResultBuilder.<PublisherTask<T>>ok(task)
                    : DefaultResultBuilder.<PublisherTask<T>>fail(result.seed());
        }
        return DefaultResultBuilder.<PublisherTask<T>>fail(Code.IS_BLOCKED.getValue());
    }

    @Override
    public Result<Object> block() {
        return switchBlock(true, Code.ALREADY_BLOCKED);
    }

    @Override
    public Result<Object> blockOut() {
        return switchBlock(false, Code.ALREADY_BLOCKED_OUT);
    }

    private Result<Object> switchBlock(final boolean newValue, final Code code){
        boolean setResult = block.compareAndSet(!newValue, newValue);
        return setResult
                ? DefaultResultBuilder.<Object>ok(null)
                : DefaultResultBuilder.<Object>fail(code.getValue());
    }
}
