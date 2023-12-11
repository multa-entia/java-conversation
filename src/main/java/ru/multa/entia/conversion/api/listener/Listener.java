package ru.multa.entia.conversion.api.listener;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.results.api.result.Result;

// TODO: 11.12.2023 ??? impl
public interface Listener<T extends ConversationItem> {
    Result<T> listen(ListenerTask<T> task);
}
