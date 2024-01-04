package ru.multa.entia.conversion.impl.listener;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.conversion.api.listener.ListenerTaskBuilder;
import ru.multa.entia.conversion.api.listener.ListenerTaskCreator;

import java.util.Objects;

public class DefaultListenerTaskBuilder<T extends ConversationItem> implements ListenerTaskBuilder<T> {

    private final ListenerTaskCreator<T> creator;

    private T item;

    public DefaultListenerTaskBuilder() {
        this(null);
    }

    public DefaultListenerTaskBuilder(final ListenerTaskCreator<T> creator) {
        this.creator = Objects.requireNonNullElse(creator, new DefaultListenerTaskCreator<>());
    }

    @Override
    public ListenerTaskBuilder<T> item(T item) {
        this.item = item;
        return this;
    }

    @Override
    public ListenerTask<T> build() {
        return creator.create(item);
    }
}
