package ru.multa.entia.conversion.impl.listener;

import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.listener.Listener;
import ru.multa.entia.conversion.api.listener.ListenerStrategy;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.results.api.result.Result;

@RequiredArgsConstructor
public class DefaultMessageListener implements Listener<Message> {
    private final ListenerStrategy<Message> strategy;

    @Override
    public Result<Message> listen(ListenerTask<Message> task) {
        return strategy.execute(task.item());
    }
}
