package ru.multa.entia.conversion.api.type;

import ru.multa.entia.results.api.result.Result;

public interface TypeFactory<T> {
    Result<Type> create(T instance, Object... args);
}
