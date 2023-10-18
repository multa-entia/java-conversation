package ru.multa.entia.conversion.api.holder;

import ru.multa.entia.conversion.api.confirmation.Confirmation;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.results.api.result.Result;

public interface Holder {
    Result<HolderItem> hold(Message message);
    Result<HolderItem> hold(Message message, HolderTimeoutStrategy timeoutStrategy, HolderReleaseStrategy releaseStrategy);
    Result<HolderItem> release(Confirmation confirmation);
}
