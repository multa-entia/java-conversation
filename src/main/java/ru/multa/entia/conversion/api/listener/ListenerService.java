package ru.multa.entia.conversion.api.listener;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.results.api.result.Result;

public interface ListenerService<T extends ConversationItem> {
    Result<ListenerTask<T>> listen(T item);
    Result<ListenerTask<T>> listen(ListenerTask<T> task);
    ListenerTaskBuilder<T> builder();
}
