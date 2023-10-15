package ru.multa.entia.conversion.impl.message;

import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.message.MessageCreator;

import java.util.UUID;

class DefaultMessageCreator implements MessageCreator {
    @Override
    public Message create(final UUID id,
                          final UUID conversation,
                          final Address from,
                          final Address to,
                          final boolean confirm,
                          final Content content) {
        return new DefaultMessage(id, conversation, from, to, confirm, content);
    }
}
