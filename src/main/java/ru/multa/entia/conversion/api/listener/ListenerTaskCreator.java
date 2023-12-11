package ru.multa.entia.conversion.api.listener;

import ru.multa.entia.conversion.api.ConversationItem;

public interface ListenerTaskCreator<T extends ConversationItem> {
    ListenerTask<T> create(T item);
}
