package ru.multa.entia.conversion.api.listener;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.results.api.result.Result;

public interface ListenerStrategy<T extends ConversationItem> {
    Result<T> execute(T item);
}
