package ru.multa.entia.conversion.api;

import ru.multa.entia.results.api.result.Result;

public interface SimpleFactory<T> {
    Result<T> create(Object instance, Object... args);
}
