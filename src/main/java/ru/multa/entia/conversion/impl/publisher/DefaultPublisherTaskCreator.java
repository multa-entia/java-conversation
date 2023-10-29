package ru.multa.entia.conversion.impl.publisher;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.holder.HolderReleaseStrategy;
import ru.multa.entia.conversion.api.holder.HolderTimeoutStrategy;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.conversion.api.publisher.PublisherTaskCreator;

class DefaultPublisherTaskCreator<T extends ConversationItem> implements PublisherTaskCreator<T> {
    @Override
    public PublisherTask<T> create(final T item,
                                   final HolderTimeoutStrategy timeoutStrategy,
                                   final HolderReleaseStrategy releaseStrategy) {
        return new DefaultPublisherTask<>(item, timeoutStrategy, releaseStrategy);
    }
}
