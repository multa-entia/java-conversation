package ru.multa.entia.conversion.api.message;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.content.Content;

public interface Message extends ConversationItem {
    boolean confirm();
    Content content();
}
