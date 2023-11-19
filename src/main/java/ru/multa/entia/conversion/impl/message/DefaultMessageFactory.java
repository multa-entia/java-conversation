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
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
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
        AtomicReference<Result<Content>> contentResult = new AtomicReference<>();
        AtomicReference<Result<UUID>> idResult = new AtomicReference<>();
        AtomicReference<Result<UUID>> conversationResult = new AtomicReference<>();
        AtomicReference<Result<Address>> fromResult = new AtomicReference<>();
        AtomicReference<Result<Address>> toResult = new AtomicReference<>();
        AtomicReference<Result<Boolean>> confirmResult = new AtomicReference<>();

        return DefaultResultBuilder.<Message>compute(
                () -> {
                    return creator.create(
                            idResult.get().value(),
                            conversationResult.get().value(),
                            fromResult.get().value(),
                            toResult.get().value(),
                            confirmResult.get().value(),
                            contentResult.get().value()
                    );
                },
                () -> {
                    return checker.check(instance);
                },
                () -> {
                    contentResult.set(contentFactory.create(instance, args));
                    return contentResult.get().ok() ? null : contentResult.get().seed();
                },
                () -> {
                    idResult.set(idGetter.apply(args));
                    return idResult.get().ok() ? null : idResult.get().seed();
                },
                () -> {
                    conversationResult.set(conversationGetter.apply(args));
                    return conversationResult.get().ok() ? null : conversationResult.get().seed();
                },
                () -> {
                    fromResult.set(fromGetter.apply(args));
                    return fromResult.get().ok() ? null : fromResult.get().seed();
                },
                () -> {
                    toResult.set(toGetter.apply(args));
                    return toResult.get().ok() ? null : toResult.get().seed();
                },
                () -> {
                    confirmResult.set(confirmGetter.apply(args));
                    return confirmResult.get().ok() ? null : confirmResult.get().seed();
                }
        );
    }
}
