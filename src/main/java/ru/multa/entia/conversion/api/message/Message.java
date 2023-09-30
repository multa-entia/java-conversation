package ru.multa.entia.conversion.api.message;

import ru.multa.entia.conversion.api.content.Content;

import java.util.UUID;

public interface Message {
    UUID id();
    boolean isRequest();
    Content content();
}
