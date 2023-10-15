package ru.multa.entia.conversion.impl.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.conversion.api.SimpleFactory;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.results.api.result.Result;

import java.util.UUID;
import java.util.function.Function;

public class DefaultMessageFactory implements SimpleFactory<Object, Message> {
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

    private final Checker<Object> checker;
    private final SimpleFactory<Object, Content> contentFactory;
    private final Function<Object[], Result<UUID>> idGetter;
    private final Function<Object[], Result<UUID>> conversationGetter;
    private final Function<Object[], Result<Address>> fromGetter;
    private final Function<Object[], Result<Address>> toGetter;
    private final Function<Object[], Result<Boolean>> confirmGetter;

    public DefaultMessageFactory() {
        this(null, null, null, null, null, null, null);
    }

    public DefaultMessageFactory(final Checker<Object> checker,
                                 final SimpleFactory<Object, Content> contentFactory,
                                 final Function<Object[], Result<UUID>> idGetter,
                                 final Function<Object[], Result<UUID>> conversationGetter,
                                 final Function<Object[], Result<Address>> fromGetter,
                                 final Function<Object[], Result<Address>> toGetter,
                                 final Function<Object[], Result<Boolean>> confirmGetter) {
        this.checker = checker;
        this.contentFactory = contentFactory;
        this.idGetter = idGetter;
        this.conversationGetter = conversationGetter;
        this.fromGetter = fromGetter;
        this.toGetter = toGetter;
        this.confirmGetter = confirmGetter;
    }

    @Override
    public Result<Message> create(final Object instance, final Object... args) {
        return null;
    }
}
