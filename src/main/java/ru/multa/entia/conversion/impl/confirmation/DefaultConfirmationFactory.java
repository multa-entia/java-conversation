package ru.multa.entia.conversion.impl.confirmation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.conversion.api.SimpleFactory;
import ru.multa.entia.conversion.api.address.AddressDecorator;
import ru.multa.entia.conversion.api.confirmation.Confirmation;
import ru.multa.entia.conversion.api.confirmation.ConfirmationCreator;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.impl.address.DefaultAddressDecorator;
import ru.multa.entia.conversion.impl.getter.DefaultArgsGetter;
import ru.multa.entia.conversion.impl.getter.DefaultValueGetter;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class DefaultConfirmationFactory implements SimpleFactory<Message, Confirmation> {
    @RequiredArgsConstructor
    @Getter
    public enum Key {
        FROM_DECORATOR("default-confirmation-factory.key.from-decorator"),
        CODE("default-confirmation-factory.key.code"),
        ARGS("default-confirmation-factory.key.args");

        private final String value;
    }

    private final Checker<Message> checker;
    private final ConfirmationCreator creator;
    private final Function<Object[], Result<AddressDecorator>> fromDecoratorGetter;
    private final Function<Object[], Result<String>> codeGetter;
    private final Function<Object[], Result<Object[]>> argsGetter;

    public DefaultConfirmationFactory() {
        this(null, null, null, null, null);
    }

    public DefaultConfirmationFactory(final Checker<Message> checker,
                                      final ConfirmationCreator creator,
                                      final Function<Object[], Result<AddressDecorator>> fromDecoratorGetter,
                                      final Function<Object[], Result<String>> codeGetter,
                                      final Function<Object[], Result<Object[]>> argsGetter) {
        this.checker = Objects.requireNonNullElse(checker, new DefaultConfirmationChecker());
        this.creator = Objects.requireNonNullElse(creator, new DefaultConfirmationCreator());
        this.fromDecoratorGetter = Objects.requireNonNullElse(
                fromDecoratorGetter,
                new DefaultValueGetter<>(Key.FROM_DECORATOR, DefaultAddressDecorator::new));
        this.codeGetter = Objects.requireNonNullElse(codeGetter, new DefaultValueGetter<>(Key.CODE, () -> {return null;}));
        this.argsGetter = Objects.requireNonNullElse(argsGetter, new DefaultArgsGetter<>(Key.ARGS));
    }

    @Override
    public Result<Confirmation> create(final Message message, final Object... args) {
        AtomicReference<Result<AddressDecorator>> decoratorResult = new AtomicReference<>();
        AtomicReference<Result<String>> codeResult = new AtomicReference<>();
        AtomicReference<Result<Object[]>> argsResult = new AtomicReference<>();

        return DefaultResultBuilder.<Confirmation>compute(
                () -> {
                    return creator.create(
                            message.id(),
                            message.conversation(),
                            decoratorResult.get().value().decorate(message.to()),
                            message.from(),
                            codeResult.get().value(),
                            argsResult.get().value()
                    );
                },
                () -> {
                    return checker.check(message);
                },
                () -> {
                    decoratorResult.set(fromDecoratorGetter.apply(args));
                    return decoratorResult.get().ok() ? null : decoratorResult.get().seed();
                },
                () -> {
                    codeResult.set(codeGetter.apply(args));
                    return codeResult.get().ok() ? null : codeResult.get().seed();
                },
                () -> {
                    argsResult.set(argsGetter.apply(args));
                    return argsResult.get().ok() ? null : argsResult.get().seed();
                }
        );
    }
}
