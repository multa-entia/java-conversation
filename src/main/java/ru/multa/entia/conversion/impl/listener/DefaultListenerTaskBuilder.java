package ru.multa.entia.conversion.impl.listener;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.listener.ListenerService;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.conversion.api.listener.ListenerTaskBuilder;
import ru.multa.entia.conversion.api.listener.ListenerTaskCreator;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.Objects;

class DefaultListenerTaskBuilder<T extends ConversationItem> implements ListenerTaskBuilder<T> {
    @RequiredArgsConstructor
    @Getter
    public enum Code {
        SERVICE_IS_NULL("default-listener-task-builder.service-is-null");

        private final String value;
    }

    private final ListenerService<T> service;
    private final ListenerTaskCreator<T> creator;

    private T item;

    public DefaultListenerTaskBuilder() {
        this(null, null);
    }

    public DefaultListenerTaskBuilder(final ListenerService<T> service) {
        this(service, null);
    }

    public DefaultListenerTaskBuilder(final ListenerService<T> service,
                                      final ListenerTaskCreator<T> creator) {
        this.service = service;
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

    @Override
    public Result<ListenerTask<T>> buildAndListen() {
        return service == null
                ? DefaultResultBuilder.<ListenerTask<T>>fail(Code.SERVICE_IS_NULL.getValue())
                : service.listen(build());
    }
}
