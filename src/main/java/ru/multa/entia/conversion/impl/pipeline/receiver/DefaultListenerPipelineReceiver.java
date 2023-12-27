package ru.multa.entia.conversion.impl.pipeline.receiver;

import lombok.extern.slf4j.Slf4j;
import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.listener.ListenerTask;

import java.util.*;

@Slf4j
public class DefaultListenerPipelineReceiver<T extends ConversationItem> extends AbstractPipelineReceiver<T, ListenerTask<T>> {
    public static final EnumMap<Code, String> CODES = new EnumMap<Code, String>(Code.class){{
        put(Code.ALREADY_BLOCKED_OUT, "default-listener-receiver.already-blocked-out");
        put(Code.ALREADY_BLOCKED, "default-listener-receiver.already-blocked");
        put(Code.ALREADY_SUBSCRIBED, "default-listener-receiver.already-subscribed");
        put(Code.ALREADY_UNSUBSCRIBED, "default-listener-receiver.already-unsubscribed");
        put(Code.IS_BLOCKED, "default-listener-receiver.is-blocked");
        put(Code.INVALID_SESSION_ID, "default-listener-receiver.invalid-session-id");
        put(Code.NO_ONE_SUBSCRIBER, "default-listener-receiver.no-one-subscriber");
        put(Code.SUBSCRIBER_FAIL, "default-listener-receiver.subscriber-fail");
    }};

    @Override
    protected String getCode(final Code code) {
        return CODES.get(code);
    }
}
