package ru.multa.entia.conversion.api.content;

import ru.multa.entia.conversion.api.type.Type;

public interface ContentCreator {
    Content create(Type type, String value);
}
