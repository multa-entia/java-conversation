package ru.multa.entia.conversion.impl.message;

import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.message.MessageOld;

import java.util.UUID;

// TODO: 01.10.2023 rename
record DefaultMessageOld(UUID id, boolean isRequest, Content content) implements MessageOld {}
