package ru.multa.entia.conversion.impl.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.conversion.api.SimpleFactory;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.message.MessageCreator;
import ru.multa.entia.conversion.impl.content.DefaultContentFactory;
import ru.multa.entia.conversion.impl.getter.DefaultConditionGetter;
import ru.multa.entia.conversion.impl.getter.DefaultValueGetter;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @RequiredArgsConstructor
    @Getter
    public enum Code {
        FROM_ABSENCE("default-message-factory.from-absence"),
        TO_ABSENCE("default-message-factory.to-absence");

        private final String value;
    }

    private final Checker<Object> checker;
    private final SimpleFactory<Object, Content> contentFactory;
    private final Function<Object[], Result<UUID>> idGetter;
    private final Function<Object[], Result<UUID>> conversationGetter;
    private final Function<Object[], Result<Address>> fromGetter;
    private final Function<Object[], Result<Address>> toGetter;
    private final Function<Object[], Result<Boolean>> confirmGetter;
    private final MessageCreator creator;

    public DefaultMessageFactory() {
        this(null, null, null, null, null, null, null, null);
    }

    public DefaultMessageFactory(final Checker<Object> checker,
                                 final SimpleFactory<Object, Content> contentFactory,
                                 final Function<Object[], Result<UUID>> idGetter,
                                 final Function<Object[], Result<UUID>> conversationGetter,
                                 final Function<Object[], Result<Address>> fromGetter,
                                 final Function<Object[], Result<Address>> toGetter,
                                 final Function<Object[], Result<Boolean>> confirmGetter,
                                 final MessageCreator creator) {
        this.checker = Objects.requireNonNullElse(checker, new DefaultMessageChecker());
        this.contentFactory = Objects.requireNonNullElse(contentFactory, new DefaultContentFactory());
        this.idGetter = Objects.requireNonNullElse(idGetter, new DefaultValueGetter<>(Key.ID, UUID::randomUUID));
        this.conversationGetter = Objects.requireNonNullElse(
                conversationGetter,
                new DefaultValueGetter<>(Key.CONVERSATION, UUID::randomUUID)
        );
        this.fromGetter = Objects.requireNonNullElse(
                fromGetter,
                new DefaultConditionGetter<>(Key.FROM, object -> {
                    return object != null && Arrays.stream(object.getClass().getInterfaces()).collect(Collectors.toSet()).contains(Address.class)
                            ? null
                            : new DefaultSeedBuilder<Address>().code(Code.FROM_ABSENCE.getValue()).build();
                })
        );
        this.toGetter = Objects.requireNonNullElse(
                toGetter,
                new DefaultConditionGetter<>(Key.TO, object -> {
                    return object != null && Arrays.stream(object.getClass().getInterfaces()).collect(Collectors.toSet()).contains(Address.class)
                            ? null
                            : new DefaultSeedBuilder<Address>().code(Code.TO_ABSENCE.getValue()).build();
                })
        );
        this.confirmGetter = Objects.requireNonNullElse(confirmGetter, new DefaultValueGetter<>(Key.CONFIRM, () -> {return true;}));
        this.creator = Objects.requireNonNullElse(creator, new DefaultMessageCreator());
    }

    @Override
    public Result<Message> create(final Object instance, final Object... args) {
        Seed seed = checker.check(instance);
        if (seed != null){
            return DefaultResultBuilder.<Message>fail(seed);
        }

        Result<Content> contentResult = contentFactory.create(instance, args);
        if (!contentResult.ok()){
            return DefaultResultBuilder.<Message>fail(contentResult.seed());
        }

        Result<UUID> idResult = idGetter.apply(args);
        if (!idResult.ok()){
            return DefaultResultBuilder.<Message>fail(idResult.seed());
        }

        Result<UUID> conversationResult = conversationGetter.apply(args);
        if (!conversationResult.ok()){
            return DefaultResultBuilder.<Message>fail(conversationResult.seed());
        }

        Result<Address> fromResult = fromGetter.apply(args);
        if (!fromResult.ok()){
            return DefaultResultBuilder.<Message>fail(fromResult.seed());
        }

        Result<Address> toResult = toGetter.apply(args);
        if (!toResult.ok()){
            return DefaultResultBuilder.<Message>fail(toResult.seed());
        }

        Result<Boolean> confirmResult = confirmGetter.apply(args);
        if (!confirmResult.ok()){
            return DefaultResultBuilder.<Message>fail(confirmResult.seed());
        }

        return DefaultResultBuilder.<Message>ok(
                creator.create(
                        idResult.value(),
                        conversationResult.value(),
                        fromResult.value(),
                        toResult.value(),
                        confirmResult.value(),
                        contentResult.value()
                )
        );
    }
}
