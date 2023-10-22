package ru.multa.entia.conversion.api.publisher;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.holder.HolderReleaseStrategy;
import ru.multa.entia.conversion.api.holder.HolderTimeoutStrategy;
import ru.multa.entia.results.api.result.Result;

public interface PublisherTask<T extends ConversationItem> {
    Publisher<T> item();
    HolderTimeoutStrategy timeoutStrategy();
    HolderReleaseStrategy releaseStrategy();
    Result<T> publish();
}
