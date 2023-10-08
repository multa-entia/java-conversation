package ru.multa.entia.conversion.impl.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.SimpleFactory;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.results.api.result.Result;

import java.util.UUID;
import java.util.function.Function;

public class DefaultMessageFactory implements SimpleFactory<Content, Message> {
    @RequiredArgsConstructor
    @Getter
    public enum Keys{
        ID("default-message-factory.id"),
        CONVERSATION("default-message-factory.conversation"),
        CONFIRM("default-message-factory.confirm"),
        FROM("default-message-factory.from"),
        TO("default-message-factory.to");

        private final String value;
    }

    @Override
    public Result<Message> create(final Content instance, final Object... args) {
        return null;
    }
}
