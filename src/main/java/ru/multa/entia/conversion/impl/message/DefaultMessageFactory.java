package ru.multa.entia.conversion.impl.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.SimpleFactory;
import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.results.api.result.Result;

public class DefaultMessageFactory implements SimpleFactory<Content, Message> {
    @RequiredArgsConstructor
    @Getter
    public enum Key {
        ID("default-message-factory.key.id"),
        CONVERSATION("default-message-factory.key.conversation"),
        CONFIRM("default-message-factory.key.confirm"),
        FROM("default-message-factory.key.from"),
        TO("default-message-factory.key.to");

        private final String value;
    }

    @Override
    public Result<Message> create(final Content instance, final Object... args) {
        return null;
    }
}
