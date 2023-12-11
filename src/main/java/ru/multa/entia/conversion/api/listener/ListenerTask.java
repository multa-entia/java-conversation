package ru.multa.entia.conversion.api.listener;

import ru.multa.entia.conversion.api.ConversationItem;

public interface ListenerTask<T extends ConversationItem> {
    T item();
}
