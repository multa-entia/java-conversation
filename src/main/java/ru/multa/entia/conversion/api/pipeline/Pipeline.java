package ru.multa.entia.conversion.api.pipeline;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.publisher.Publisher;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.results.api.result.Result;

public interface Pipeline<T extends ConversationItem> {
    Result<PublisherTask<T>> offer(PublisherTask<T> task);
    Result<Publisher<T>> subscribe(Publisher<T> subscriber);
    Result<Publisher<T>> unsubscribe(Publisher<T> subscriber);
}
