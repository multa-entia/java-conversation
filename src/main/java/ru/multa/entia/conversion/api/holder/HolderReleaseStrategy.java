package ru.multa.entia.conversion.api.holder;

import ru.multa.entia.conversion.api.confirmation.Confirmation;
import ru.multa.entia.conversion.api.message.Message;

public interface HolderReleaseStrategy {
    void execute(Message message, Confirmation confirmation);
}
