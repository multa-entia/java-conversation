package ru.multa.entia.conversion.api.confirmation;

import ru.multa.entia.conversion.api.address.Address;

import java.util.UUID;

public interface ConfirmationCreator {
    Confirmation create(UUID id, UUID conversation, Address from, Address to, String code, Object[] args);
}
