package ru.multa.entia.conversion.api.publisher;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.holder.HolderReleaseStrategy;
import ru.multa.entia.conversion.api.holder.HolderTimeoutStrategy;

public interface PublisherTaskCreator<T extends ConversationItem> {
    PublisherTask<T> create(T item, HolderTimeoutStrategy timeoutStrategy, HolderReleaseStrategy releaseStrategy);
}
