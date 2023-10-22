package ru.multa.entia.conversion.api.publisher;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.holder.HolderReleaseStrategy;
import ru.multa.entia.conversion.api.holder.HolderTimeoutStrategy;
import ru.multa.entia.results.api.result.Result;

public interface PublisherTaskBuilder<T extends ConversationItem> {
    PublisherTask<T> item(T item);
    PublisherTask<T> timeoutStrategy(HolderTimeoutStrategy strategy);
    PublisherTask<T> releaseStrategy(HolderReleaseStrategy strategy);
    PublisherTask<T> useDefaultStrategy();
    PublisherTask<T> doNotUseStrategy();
    PublisherTask<T> build();
    Result<T> buildAndPublish();
}
