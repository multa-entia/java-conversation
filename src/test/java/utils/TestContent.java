package utils;

import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.type.Type;

public record TestContent(Type type, String value) implements Content {}
