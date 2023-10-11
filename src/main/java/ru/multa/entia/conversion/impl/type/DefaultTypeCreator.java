package ru.multa.entia.conversion.impl.type;

import ru.multa.entia.conversion.api.type.Type;
import ru.multa.entia.conversion.api.type.TypeCreator;

class DefaultTypeCreator implements TypeCreator {
    @Override
    public Type create(final String value) {
        return new DefaultType(value);
    }
}
