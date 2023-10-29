package ru.multa.entia.conversion.api.publisher;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.holder.HolderReleaseStrategy;
import ru.multa.entia.conversion.api.holder.HolderTimeoutStrategy;
import ru.multa.entia.results.api.result.Result;

public interface PublisherTaskBuilder<T extends ConversationItem> {
    PublisherTaskBuilder<T> item(T item);
    PublisherTaskBuilder<T> timeoutStrategy(HolderTimeoutStrategy strategy);
    PublisherTaskBuilder<T> releaseStrategy(HolderReleaseStrategy strategy);
    PublisherTaskBuilder<T> useSetStrategy();
    PublisherTaskBuilder<T> useDefaultStrategy();
    PublisherTaskBuilder<T> doNotUseStrategy();
    PublisherTask<T> build();
    Result<PublisherTask<T>> buildAndPublish();
}
