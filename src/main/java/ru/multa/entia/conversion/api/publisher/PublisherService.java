package ru.multa.entia.conversion.api.publisher;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.results.api.result.Result;

// TODO: 22.10.2023 !!! implementation must contain class which wrap queue
public interface PublisherService<T extends ConversationItem> {
    Result<T> publish(T item);
    Result<T> publish(PublisherTask<T> task);
    PublisherTaskBuilder<T> builder();
}
