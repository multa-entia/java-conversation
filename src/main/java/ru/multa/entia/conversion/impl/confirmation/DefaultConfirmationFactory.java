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
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

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
        this.checker = checker == null ? new DefaultConfirmationChecker() : checker;
        this.creator = creator == null ? new DefaultConfirmationCreator() : creator;
        this.fromDecoratorGetter = fromDecoratorGetter == null
                ? new DefaultValueGetter<>(Key.FROM_DECORATOR, DefaultAddressDecorator::new)
                : fromDecoratorGetter;
        this.codeGetter = codeGetter == null
                ? new DefaultValueGetter<>(Key.CODE, () -> {return null;})
                : codeGetter;
        this.argsGetter = argsGetter == null
                ? new DefaultArgsGetter<>(Key.ARGS)
                : argsGetter;
    }

    @Override
    public Result<Confirmation> create(final Message message, final Object... args) {
        Seed seed = checker.check(message);
        if (seed != null){
            return DefaultResultBuilder.<Confirmation>fail(seed);
        }

        Result<AddressDecorator> decoratorResult = fromDecoratorGetter.apply(args);
        if (!decoratorResult.ok()){
            return DefaultResultBuilder.<Confirmation>fail(decoratorResult.seed());
        }

        Result<String> codeResult = codeGetter.apply(args);
        if (!codeResult.ok()){
            return DefaultResultBuilder.<Confirmation>fail(codeResult.seed());
        }

        Result<Object[]> argsResult = argsGetter.apply(args);
        if (!argsResult.ok()){
            return DefaultResultBuilder.<Confirmation>fail(argsResult.seed());
        }

        Confirmation confirmation = creator.create(
                message.id(),
                message.conversation(),
                decoratorResult.value().decorate(message.to()),
                message.from(),
                codeResult.value(),
                argsResult.value()
        );
        return DefaultResultBuilder.<Confirmation>ok(confirmation);
    }
}
