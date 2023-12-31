package ru.multa.entia.conversion.impl.publisher;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.holder.HolderReleaseStrategy;
import ru.multa.entia.conversion.api.holder.HolderTimeoutStrategy;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.conversion.api.publisher.PublisherTaskBuilder;
import ru.multa.entia.conversion.api.publisher.PublisherTaskCreator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class DefaultPublisherTaskBuilder<T extends ConversationItem> implements PublisherTaskBuilder<T> {

    enum StrategiesUsage {
        USE_SET,
        USE_DEFAULT,
        DO_NOT_USE
    }

    private final Map<StrategiesUsage, Function<DefaultPublisherTaskBuilder<T>, PublisherTask<T>>> creators = new HashMap<>(){{
        put(StrategiesUsage.USE_SET, DefaultPublisherTaskBuilder::<T>createForUseSetStrategy);
        put(StrategiesUsage.USE_DEFAULT, DefaultPublisherTaskBuilder::<T>createForUseDefaultStrategy);
        put(StrategiesUsage.DO_NOT_USE, DefaultPublisherTaskBuilder::<T>createForDoNotUseStrategy);
    }};

    private final Supplier<HolderTimeoutStrategy> timeoutStrategySup;
    private final Supplier<HolderReleaseStrategy> releaseStrategySup;
    private final PublisherTaskCreator<T> creator;

    private StrategiesUsage strategiesUsage = StrategiesUsage.USE_SET;
    private HolderTimeoutStrategy timeoutStrategy;
    private HolderReleaseStrategy releaseStrategy;

    private T item;

    public DefaultPublisherTaskBuilder() {
        this(null, null, null);
    }

    public DefaultPublisherTaskBuilder(final Supplier<HolderTimeoutStrategy> timeoutStrategySup,
                                       final Supplier<HolderReleaseStrategy> releaseStrategySup,
                                       final PublisherTaskCreator<T> creator) {
        this.timeoutStrategySup = timeoutStrategySup;
        this.releaseStrategySup = releaseStrategySup;
        this.creator = Objects.requireNonNullElse(creator, new DefaultPublisherTaskCreator<>());
    }

    @Override
    public PublisherTaskBuilder<T> item(final T item) {
        this.item = item;
        return this;
    }

    @Override
    public PublisherTaskBuilder<T> timeoutStrategy(final HolderTimeoutStrategy strategy) {
        this.timeoutStrategy = strategy;
        return this;
    }

    @Override
    public PublisherTaskBuilder<T> releaseStrategy(final HolderReleaseStrategy strategy) {
        this.releaseStrategy = strategy;
        return this;
    }

    @Override
    public PublisherTaskBuilder<T> useSetStrategy() {
        this.strategiesUsage = StrategiesUsage.USE_SET;
        return this;
    }

    @Override
    public PublisherTaskBuilder<T> useDefaultStrategy() {
        this.strategiesUsage = StrategiesUsage.USE_DEFAULT;
        return this;
    }

    @Override
    public PublisherTaskBuilder<T> doNotUseStrategy() {
        this.strategiesUsage = StrategiesUsage.DO_NOT_USE;
        return this;
    }

    @Override
    public PublisherTask<T> build() {
        return creators.get(strategiesUsage).apply(this);
    }

    private static <T extends ConversationItem> PublisherTask<T> createForDoNotUseStrategy(final DefaultPublisherTaskBuilder<T> builder) {
        return builder.creator.create(builder.item, null, null);
    }

    private static <T extends ConversationItem> PublisherTask<T> createForUseDefaultStrategy(final DefaultPublisherTaskBuilder<T> builder) {
        return builder.creator.create(builder.item, builder.timeoutStrategySup.get(), builder.releaseStrategySup.get());
    }

    private static <T extends ConversationItem> PublisherTask<T> createForUseSetStrategy(final DefaultPublisherTaskBuilder<T> builder) {
        return builder.creator.create(builder.item, builder.timeoutStrategy, builder.releaseStrategy);
    }
}
