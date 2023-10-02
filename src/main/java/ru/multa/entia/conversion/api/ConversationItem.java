package ru.multa.entia.conversion.api;

import ru.multa.entia.conversion.api.address.Address;

import java.util.UUID;

public interface ConversationItem {
    UUID id();
    UUID conversation();
    Address from();
    Address to();
}
