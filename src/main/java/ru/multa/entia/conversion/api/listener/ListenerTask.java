package ru.multa.entia.conversion.api.listener;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.Task;

public interface ListenerTask<T extends ConversationItem> extends Task<T> {}
