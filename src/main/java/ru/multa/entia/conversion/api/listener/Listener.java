package ru.multa.entia.conversion.api.listener;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.results.api.result.Result;

public interface Listener<T extends ConversationItem> {
    Result<T> listen(ListenerTask<T> task);
}
