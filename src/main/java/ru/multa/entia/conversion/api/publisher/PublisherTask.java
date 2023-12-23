package ru.multa.entia.conversion.api.publisher;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.Task;
import ru.multa.entia.conversion.api.holder.HolderReleaseStrategy;
import ru.multa.entia.conversion.api.holder.HolderTimeoutStrategy;

public interface PublisherTask<T extends ConversationItem> extends Task<T> {
    HolderTimeoutStrategy timeoutStrategy();
    HolderReleaseStrategy releaseStrategy();
}
