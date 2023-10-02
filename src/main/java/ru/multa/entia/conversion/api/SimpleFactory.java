package ru.multa.entia.conversion.api;

import ru.multa.entia.results.api.result.Result;

public interface SimpleFactory<T, R> {
    Result<R> create(T instance, Object... args);
}
