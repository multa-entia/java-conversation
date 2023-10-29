package ru.multa.entia.conversion.impl.publisher;

import ru.multa.entia.conversion.api.holder.Holder;
import ru.multa.entia.conversion.api.holder.HolderItem;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.publisher.Publisher;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.conversion.api.sender.Sender;
import ru.multa.entia.conversion.impl.holder.DefaultHolder;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

public class DefaultMessagePublisher implements Publisher<Message> {
    private final Sender<Message> sender;
    private final Holder holder;

    public DefaultMessagePublisher(final Sender<Message> sender) {
        this(sender, null);
    }

    public DefaultMessagePublisher(final Sender<Message> sender, final Holder holder) {
        this.sender = sender;
        this.holder = holder == null ? new DefaultHolder() : holder;
    }

    @Override
    public Result<Message> publish(PublisherTask<Message> task) {
        Result<Message> sendingResult = sender.send(task.item());
        if (sendingResult.ok()){
            Result<HolderItem> holdingResult = holder.hold(task.item(), task.timeoutStrategy(), task.releaseStrategy());
            if (!holdingResult.ok()){
                return DefaultResultBuilder.<Message>fail(holdingResult.seed());
            }
        }
        return sendingResult;
    }
}
