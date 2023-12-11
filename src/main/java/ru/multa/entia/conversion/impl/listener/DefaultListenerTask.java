package ru.multa.entia.conversion.impl.listener;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.listener.ListenerTask;

record DefaultListenerTask<T extends ConversationItem>(T item) implements ListenerTask<T> {}
