package ru.multa.entia.conversion.impl.listener;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.conversion.api.listener.ListenerTaskCreator;

class DefaultListenerTaskCreator<T extends ConversationItem> implements ListenerTaskCreator<T> {
    @Override
    public ListenerTask<T> create(final T item) {
        return new DefaultListenerTask<>(item);
    }
}
