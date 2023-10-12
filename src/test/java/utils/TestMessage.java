package utils;

import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.message.Message;

import java.util.UUID;

public record TestMessage(UUID id, UUID conversation, Address from, Address to, boolean confirm, Content content)
        implements Message {}
