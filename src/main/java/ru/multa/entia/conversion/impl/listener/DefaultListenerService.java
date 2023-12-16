package ru.multa.entia.conversion.impl.listener;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.listener.ListenerService;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.conversion.api.listener.ListenerTaskBuilder;
import ru.multa.entia.conversion.api.listener.ListenerTaskCreator;
import ru.multa.entia.results.api.result.Result;

import java.util.Objects;
import java.util.function.Function;

public class DefaultListenerService<T extends ConversationItem> implements ListenerService<T> {
    private final Function<ListenerTask<T>, Result<ListenerTask<T>>> output;
    private final Function<ListenerService<T>, ListenerTaskBuilder<T>> builderCreator;
    private final ListenerTaskCreator<T> taskCreator;

    public DefaultListenerService(Function<ListenerTask<T>, Result<ListenerTask<T>>> output) {
        this(output, null, null);
    }

    public DefaultListenerService(final Function<ListenerTask<T>, Result<ListenerTask<T>>> output,
                                  final Function<ListenerService<T>, ListenerTaskBuilder<T>> builderCreator,
                                  final ListenerTaskCreator<T> taskCreator) {
        this.output = output;
        this.builderCreator = Objects.requireNonNullElse(builderCreator, service -> {return new DefaultListenerTaskBuilder<>(service, null);});
        this.taskCreator = Objects.requireNonNullElse(taskCreator, new DefaultListenerTaskCreator<>());
    }

    @Override
    public Result<ListenerTask<T>> listen(final T item) {
        return listen(taskCreator.create(item));
    }

    @Override
    public Result<ListenerTask<T>> listen(final ListenerTask<T> task) {
        return output.apply(task);
    }

    @Override
    public ListenerTaskBuilder<T> builder() {
        return builderCreator.apply(this);
    }
}
