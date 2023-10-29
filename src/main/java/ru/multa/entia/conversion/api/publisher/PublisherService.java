package ru.multa.entia.conversion.api.publisher;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.results.api.result.Result;

public interface PublisherService<T extends ConversationItem> {
    Result<PublisherTask<T>> publish(T item);
    Result<PublisherTask<T>> publish(PublisherTask<T> task);
    PublisherTaskBuilder<T> builder();
}
