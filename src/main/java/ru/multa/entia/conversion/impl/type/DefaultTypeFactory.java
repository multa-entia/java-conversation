package ru.multa.entia.conversion.impl.type;

import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.conversion.api.SimpleFactory;
import ru.multa.entia.conversion.api.type.Type;
import ru.multa.entia.conversion.api.type.TypeCreator;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.Objects;

public class DefaultTypeFactory implements SimpleFactory<Object, Type> {
    private final Checker<Object> checker;
    private final TypeCreator creator;

    public DefaultTypeFactory() {
        this(null, null);
    }

    public DefaultTypeFactory(final Checker<Object> checker, final TypeCreator creator) {
        this.checker = Objects.requireNonNullElse(checker, new DefaultTypeValueChecker());
        this.creator = Objects.requireNonNullElse(creator, new DefaultTypeCreator());
    }

    @Override
    public Result<Type> create(final Object instance, final Object... args) {
        return DefaultResultBuilder.<Type>compute(
                () -> {return creator.create(String.valueOf(instance));},
                () -> {return checker.check(instance);}
        );
    }
}
