package ru.multa.entia.conversion.api.content;

import ru.multa.entia.results.api.result.Result;

public interface ContentFactory {
    Result<Content> create(Object instance, Object... args);
}
