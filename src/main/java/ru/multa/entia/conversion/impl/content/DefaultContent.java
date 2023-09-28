package ru.multa.entia.conversion.impl.content;

import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.type.Type;


record DefaultContent(Type type, String value) implements Content {}
