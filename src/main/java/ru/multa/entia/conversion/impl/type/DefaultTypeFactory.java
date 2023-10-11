package ru.multa.entia.conversion.impl.type;

import ru.multa.entia.conversion.api.SimpleFactory;
import ru.multa.entia.conversion.api.type.Type;
import ru.multa.entia.conversion.api.type.TypeCreator;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.function.Function;

public class DefaultTypeFactory implements SimpleFactory<Object, Type> {
    private final Function<Object, Seed> checker;
    private final TypeCreator creator;

    public DefaultTypeFactory() {
        this(null, null);
    }

    public DefaultTypeFactory(final Function<Object, Seed> checker, final TypeCreator creator) {
        this.checker = checker == null ? new DefaultTypeValueChecker() : checker;
        this.creator = creator == null ? new DefaultTypeCreator() : creator;
    }

    @Override
    public Result<Type> create(final Object instance, final Object... args) {
        Seed seed = checker.apply(instance);
        if (seed != null){
            return DefaultResultBuilder.<Type>fail(seed);
        }

        return DefaultResultBuilder.<Type>ok(creator.create(String.valueOf(instance)));
    }
}
