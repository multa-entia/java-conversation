package ru.multa.entia.conversion.impl.confirmation;

import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.confirmation.Confirmation;

import java.util.UUID;

record DefaultConfirmation(UUID id,
                           UUID conversation,
                           Address from,
                           Address to,
                           String code,
                           Object[] args) implements Confirmation {}

