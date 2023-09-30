package ru.multa.entia.conversion.impl.message;

import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.message.Message;

import java.util.UUID;

record DefaultMessage(UUID id, boolean isRequest, Content content) implements Message {}
