package ru.multa.entia.conversion.impl.confirmation;

import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.confirmation.Confirmation;
import ru.multa.entia.conversion.api.confirmation.ConfirmationCreator;

import java.util.UUID;

class DefaultConfirmationCreator implements ConfirmationCreator {
    @Override
    public Confirmation create(final UUID id,
                               final UUID conversation,
                               final Address from,
                               final Address to,
                               final String code,
                               final Object[] args) {
        return new DefaultConfirmation( id, conversation, from, to, code, args);
    }
}
