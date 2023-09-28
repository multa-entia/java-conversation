package ru.multa.entia.conversion.api.content;

import ru.multa.entia.conversion.api.type.Type;

public interface Content {
    Type type();
    String value();
}
