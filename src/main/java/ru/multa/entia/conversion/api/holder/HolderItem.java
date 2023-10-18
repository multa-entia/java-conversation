package ru.multa.entia.conversion.api.holder;

import ru.multa.entia.conversion.api.message.Message;

public interface HolderItem {
    Message message();
    HolderTimeoutStrategy timeoutStrategy();
    HolderReleaseStrategy releaseStrategy();
}
