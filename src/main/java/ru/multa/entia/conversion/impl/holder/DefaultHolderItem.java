package ru.multa.entia.conversion.impl.holder;

import ru.multa.entia.conversion.api.holder.HolderItem;
import ru.multa.entia.conversion.api.holder.HolderReleaseStrategy;
import ru.multa.entia.conversion.api.holder.HolderTimeoutStrategy;
import ru.multa.entia.conversion.api.message.Message;

record DefaultHolderItem(Message message, HolderTimeoutStrategy timeoutStrategy, HolderReleaseStrategy releaseStrategy)
        implements HolderItem {
}
