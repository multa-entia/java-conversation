package ru.multa.entia.conversion.api.message;

import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.content.Content;

import java.util.UUID;

public interface MessageCreator {
    Message create(UUID id,
                   UUID conversation,
                   Address from,
                   Address to,
                   boolean confirm,
                   Content content);
}
