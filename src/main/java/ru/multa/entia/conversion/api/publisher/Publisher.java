package ru.multa.entia.conversion.api.publisher;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.results.api.result.Result;

// TODO: 22.10.2023 contains implementation of sender
public interface Publisher<T extends ConversationItem> {
    Result<T> publish(PublisherTask<T> task);
}
