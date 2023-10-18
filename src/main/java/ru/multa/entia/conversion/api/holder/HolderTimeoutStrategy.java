package ru.multa.entia.conversion.api.holder;

import ru.multa.entia.conversion.api.message.Message;

import java.util.concurrent.TimeUnit;

public interface HolderTimeoutStrategy {
    void execute(Message message);
    int getTimeout();
}
