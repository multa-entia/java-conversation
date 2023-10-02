package ru.multa.entia.conversion.api.confirmation;

import ru.multa.entia.conversion.api.ConversationItem;

public interface Confirmation extends ConversationItem {
    String code();
    Object[] args();
}
