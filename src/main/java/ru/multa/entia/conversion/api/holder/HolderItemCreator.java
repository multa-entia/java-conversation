package ru.multa.entia.conversion.api.holder;

import ru.multa.entia.conversion.api.message.Message;

public interface HolderItemCreator {
    HolderItem create(Message message, HolderTimeoutStrategy timeoutStrategy, HolderReleaseStrategy releaseStrategy);
}
