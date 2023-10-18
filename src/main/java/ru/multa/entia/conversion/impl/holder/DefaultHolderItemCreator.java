package ru.multa.entia.conversion.impl.holder;

import ru.multa.entia.conversion.api.holder.HolderItem;
import ru.multa.entia.conversion.api.holder.HolderItemCreator;
import ru.multa.entia.conversion.api.holder.HolderReleaseStrategy;
import ru.multa.entia.conversion.api.holder.HolderTimeoutStrategy;
import ru.multa.entia.conversion.api.message.Message;

class DefaultHolderItemCreator implements HolderItemCreator {
    @Override
    public HolderItem create(final Message message,
                             final HolderTimeoutStrategy timeoutStrategy,
                             final HolderReleaseStrategy releaseStrategy) {
        return new DefaultHolderItem(message, timeoutStrategy, releaseStrategy);
    }
}
