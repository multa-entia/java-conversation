package ru.multa.entia.conversion.impl.publisher;

import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.confirmation.Confirmation;
import ru.multa.entia.conversion.api.publisher.Publisher;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.conversion.api.sender.Sender;
import ru.multa.entia.results.api.result.Result;

@RequiredArgsConstructor
public class DefaultConfirmationPublisher implements Publisher<Confirmation> {
    private final Sender<Confirmation> sender;

    @Override
    public Result<Confirmation> publish(final PublisherTask<Confirmation> task) {
        return sender.send(task.item());
    }
}
