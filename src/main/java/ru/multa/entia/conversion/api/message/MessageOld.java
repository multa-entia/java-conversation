package ru.multa.entia.conversion.api.message;

import ru.multa.entia.conversion.api.content.Content;

import java.util.UUID;

// TODO: 01.10.2023 rename
public interface MessageOld {
    UUID id();
    boolean isRequest();
    Content content();
}
