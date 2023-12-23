package ru.multa.entia.conversion.api.listener;

import ru.multa.entia.conversion.api.ConversationItem;
 
public interface ListenerTaskBuilder<T extends ConversationItem> {
    ListenerTaskBuilder<T> item(T item);
    ListenerTask<T> build();
}
