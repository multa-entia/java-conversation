package utils;

import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.confirmation.Confirmation;

import java.util.UUID;

public record TestConfirmation(UUID id, UUID conversation, Address from, Address to, String code, Object[] args)
        implements Confirmation {}
