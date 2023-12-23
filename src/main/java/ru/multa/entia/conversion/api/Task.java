package ru.multa.entia.conversion.api;

public interface Task<T extends ConversationItem> {
    T item();
}
