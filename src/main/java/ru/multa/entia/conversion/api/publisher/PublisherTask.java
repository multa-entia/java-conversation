package ru.multa.entia.conversion.api.publisher;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.holder.HolderReleaseStrategy;
import ru.multa.entia.conversion.api.holder.HolderTimeoutStrategy;

public interface PublisherTask<T extends ConversationItem> {
    T item();
    HolderTimeoutStrategy timeoutStrategy();
    HolderReleaseStrategy releaseStrategy();
}
