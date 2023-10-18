package ru.multa.entia.conversion.impl.holder;

import ru.multa.entia.conversion.api.confirmation.Confirmation;
import ru.multa.entia.conversion.api.holder.Holder;
import ru.multa.entia.conversion.api.holder.HolderReleaseStrategy;
import ru.multa.entia.conversion.api.holder.HolderTimeoutStrategy;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.results.api.result.Result;

public class DefaultHolder implements Holder {
    private final static int DEFAULT_SIZE = 100_000;

    private final int size;

    public DefaultHolder() {
        this(0);
    }

    public DefaultHolder(int size) {
        this.size = size > 0 ? size : DEFAULT_SIZE;
    }

    // TODO: 17.10.2023 remake sync-block
    @Override
    public synchronized Result<Message> hold(final Message message) {
        return hold(message, null, null);
    }

    // TODO: 17.10.2023 remake sync-block
    @Override
    public synchronized Result<Message> hold(final Message message,
                                             final HolderTimeoutStrategy timeoutStrategy,
                                             final HolderReleaseStrategy releaseStrategy) {
        return null;
    }

    // TODO: 17.10.2023 remake sync-block
    @Override
    public synchronized Result<Message> release(Confirmation confirmation) {
        return null;
    }
}
