package ru.multa.entia.conversion.impl.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.holder.HolderReleaseStrategy;
import ru.multa.entia.conversion.api.holder.HolderTimeoutStrategy;
import ru.multa.entia.conversion.api.publisher.PublisherService;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.conversion.api.publisher.PublisherTaskBuilder;
import ru.multa.entia.results.api.result.Result;

import java.util.function.Supplier;

@Slf4j
class DefaultPublisherTaskBuilder<T extends ConversationItem> implements PublisherTaskBuilder<T> {
    private final Supplier<HolderTimeoutStrategy> timeoutStrategySup;
    private final Supplier<HolderReleaseStrategy> releaseStrategySup;
    private final PublisherService<T> service;

    private StrategiesUsage strategiesUsage = StrategiesUsage.USE_SET;

    private T item;

    public DefaultPublisherTaskBuilder() {
        this(null, null, null);
    }

    public DefaultPublisherTaskBuilder(final Supplier<HolderTimeoutStrategy> timeoutStrategySup,
                                       final Supplier<HolderReleaseStrategy> releaseStrategySup,
                                       final PublisherService<T> service) {
        this.timeoutStrategySup = timeoutStrategySup;
        this.releaseStrategySup = releaseStrategySup;
        this.service = service;
    }

    @Override
    public PublisherTaskBuilder<T> item(T item) {
        this.item = item;
        return this;
    }

    @Override
    public PublisherTaskBuilder<T> timeoutStrategy(HolderTimeoutStrategy strategy) {
        return null;
    }

    @Override
    public PublisherTaskBuilder<T> releaseStrategy(HolderReleaseStrategy strategy) {
        return null;
    }

    @Override
    public PublisherTaskBuilder<T> useSetStrategy() {
        return null;
    }

    @Override
    public PublisherTaskBuilder<T> useDefaultStrategy() {
        return null;
    }

    @Override
    public PublisherTaskBuilder<T> doNotUseStrategy() {
        return null;
    }

    @Override
    public PublisherTask<T> build() {
        return null;
    }

    @Override
    public Result<T> buildAndPublish() {
        return null;
    }

    enum StrategiesUsage{
        USE_SET,
        USE_DEFAULT,
        DO_NOT_USE
    }
}
