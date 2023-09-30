package ru.multa.entia.conversion.api.message;

import ru.multa.entia.results.api.result.Result;

public interface MessageFactory {
    Result<Message> create(Object instance, Object... args);
}
