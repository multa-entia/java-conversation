package ru.multa.entia.conversion.api.listener;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.results.api.result.Result;

public interface ListenerTaskBuilder<T extends ConversationItem> {
    ListenerTaskBuilder<T> item(T item);
    ListenerTask<T> build();
    Result<ListenerTask<T>> buildAndListen();
}
