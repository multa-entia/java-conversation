package ru.multa.entia.conversion.impl.content;

import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.conversion.api.SimpleFactory;
import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.content.ContentCreator;
import ru.multa.entia.conversion.api.type.Type;
import ru.multa.entia.conversion.impl.type.DefaultTypeFactory;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
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
        this.typeFactory = Objects.requireNonNullElse(typeFactory, new DefaultTypeFactory());
        this.checker = Objects.requireNonNullElse(checker, new DefaultContentChecker());
        this.serializer = Objects.requireNonNullElse(serializer, new DefaultContentSerializer());
        this.creator = Objects.requireNonNullElse(creator, new DefaultContentCreator());
    }

    @Override
    public Result<Content> create(final Object instance, final Object... args) {
        AtomicReference<Result<Type>> typeFactoryResult = new AtomicReference<>();
        AtomicReference<Result<String>> serializeResult = new AtomicReference<>();

        return DefaultResultBuilder.<Content>compute(
                () -> {
                    return creator.create(typeFactoryResult.get().value(), serializeResult.get().value());
                },
                () -> {
                    typeFactoryResult.set(typeFactory.create(instance, args));
                    return typeFactoryResult.get().ok() ? null : typeFactoryResult.get().seed();
                },
                () -> {
                    return checker.check(instance);
                },
                () -> {
                    serializeResult.set(serializer.apply(instance));
                    return serializeResult.get().ok() ? null : serializeResult.get().seed();
                }
        );
    }
}
