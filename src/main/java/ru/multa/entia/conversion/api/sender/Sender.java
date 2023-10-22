package ru.multa.entia.conversion.api.sender;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.results.api.result.Result;

public interface Sender<T extends ConversationItem>{
    Result<T> send(T conversationItem);
}
