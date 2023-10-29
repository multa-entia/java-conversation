package ru.multa.entia.conversion.impl.publisher;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.holder.HolderReleaseStrategy;
import ru.multa.entia.conversion.api.holder.HolderTimeoutStrategy;
import ru.multa.entia.conversion.api.publisher.PublisherService;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.conversion.api.publisher.PublisherTaskBuilder;
import ru.multa.entia.conversion.api.publisher.PublisherTaskCreator;
import ru.multa.entia.results.api.result.Result;

import java.util.function.Function;
import java.util.function.Supplier;

public class DefaultPublisherService<T extends ConversationItem> implements PublisherService<T> {
    private final Function<PublisherTask<T>, Result<PublisherTask<T>>> output;
    private final Supplier<HolderTimeoutStrategy> timeoutStrategySupplier;
    private final Supplier<HolderReleaseStrategy> releaseStrategySupplier;
    private final Function<PublisherService<T>, PublisherTaskBuilder<T>> builderCreator;
    private final PublisherTaskCreator<T> taskCreator;

    public DefaultPublisherService(final Function<PublisherTask<T>, Result<PublisherTask<T>>> output) {
        this(output, null, null, null, null);
    }

    public DefaultPublisherService(final Function<PublisherTask<T>, Result<PublisherTask<T>>> output,
                                   final Supplier<HolderTimeoutStrategy> timeoutStrategySupplier,
                                   final Supplier<HolderReleaseStrategy> releaseStrategySupplier,
                                   final Function<PublisherService<T>, PublisherTaskBuilder<T>> builderCreator,
                                   final PublisherTaskCreator<T> taskCreator) {
        this.output = output;
        this.timeoutStrategySupplier = timeoutStrategySupplier == null ? () -> {return null;} : timeoutStrategySupplier;
        this.releaseStrategySupplier = releaseStrategySupplier == null ? () -> {return null;} : releaseStrategySupplier;
        this.builderCreator = checkOrCreateBuilderCreator(
                builderCreator,
                this.timeoutStrategySupplier,
                this.releaseStrategySupplier);
        this.taskCreator = taskCreator == null ? new DefaultPublisherTaskCreator<>() : taskCreator;
    }

    @Override
    public Result<PublisherTask<T>> publish(T item) {
        return publish(taskCreator.create(item, timeoutStrategySupplier.get(), releaseStrategySupplier.get()));
    }

    @Override
    public Result<PublisherTask<T>> publish(PublisherTask<T> task) {
        return output.apply(task);
    }

    @Override
    public PublisherTaskBuilder<T> builder() {
        return builderCreator.apply(this);
    }

    private Function<PublisherService<T>, PublisherTaskBuilder<T>> checkOrCreateBuilderCreator(
            final Function<PublisherService<T>,PublisherTaskBuilder<T>> builderCreator,
            final Supplier<HolderTimeoutStrategy> timeoutStrategySupplier,
            final Supplier<HolderReleaseStrategy> releaseStrategySupplier) {
        if (builderCreator != null) {
            return builderCreator;
        }

        return service -> {
            return new DefaultPublisherTaskBuilder<>(
                    timeoutStrategySupplier,
                    releaseStrategySupplier,
                    this,
                    null);
        };
    }
}
