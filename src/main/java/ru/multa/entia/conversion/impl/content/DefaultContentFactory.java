package ru.multa.entia.conversion.impl.content;

import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.conversion.api.SimpleFactory;
import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.content.ContentCreator;
import ru.multa.entia.conversion.api.type.Type;
import ru.multa.entia.conversion.impl.type.DefaultTypeFactory;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.function.Function;

public class DefaultContentFactory implements SimpleFactory<Object, Content> {
    private final SimpleFactory<Object, Type> typeFactory;
    private final Checker<Object> checker;
    private final Function<Object, Result<String>> serializer;
    private final ContentCreator creator;

    public DefaultContentFactory() {
        this(null, null, null, null);
    }

    public DefaultContentFactory(final SimpleFactory<Object, Type> typeFactory,
                                 final Checker<Object> checker,
                                 final Function<Object,Result<String>> serializer,
                                 final ContentCreator creator) {
        this.typeFactory = typeFactory == null ? new DefaultTypeFactory() : typeFactory;
        this.checker = checker == null ? new DefaultContentChecker() : checker;
        this.serializer = serializer == null ? new DefaultContentSerializer() : serializer;
        this.creator = creator == null ? new DefaultContentCreator() : creator;
    }

    @Override
    public Result<Content> create(final Object instance, final Object... args) {
        Result<Type> result = typeFactory.create(instance, args);
        if (!result.ok()){
            return DefaultResultBuilder.<Content>fail(result.seed());
        }

        Seed seed = checker.check(instance);
        if (seed != null){
            return DefaultResultBuilder.<Content>fail(seed);
        }

        Result<String> serializeResult = serializer.apply(instance);
        if (!serializeResult.ok()){
            return DefaultResultBuilder.<Content>fail(serializeResult.seed());
        }

        return DefaultResultBuilder.<Content>ok(creator.create(result.value(), serializeResult.value()));
    }
}
