package ru.multa.entia.conversion.impl.content;

import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.content.ContentCreator;
import ru.multa.entia.conversion.api.type.Type;

class DefaultContentCreator implements ContentCreator {
    @Override
    public Content create(final Type type, final String value) {
        return new DefaultContent(type, value);
    }
}
