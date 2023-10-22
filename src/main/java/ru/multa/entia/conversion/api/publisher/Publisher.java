package ru.multa.entia.conversion.api.publisher;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.results.api.result.Result;

public interface Publisher<T extends ConversationItem> {
    Result<T> publish(T conversationItem);
    Result<T> publish(PublisherTask<T> task);
    PublisherTaskBuilder<T> builder();
}
