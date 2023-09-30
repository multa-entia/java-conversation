package ru.multa.entia.conversion.impl.type;

import ru.multa.entia.conversion.api.type.Type;
import ru.multa.entia.conversion.api.type.TypeFactory;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

public class DefaultTypeFactory implements TypeFactory<Object> {
    public static final String CODE = "conversation.factory.type.instance-is-null";

    @Override
    public Result<Type> create(Object instance, Object... args) {
        return instance != null
                ? DefaultResultBuilder.<Type>ok(new DefaultType(instance.getClass().getName()))
                : DefaultResultBuilder.<Type>fail(CODE);
    }
}
