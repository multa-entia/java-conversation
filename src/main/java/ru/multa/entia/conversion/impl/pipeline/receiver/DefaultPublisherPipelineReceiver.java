package ru.multa.entia.conversion.impl.pipeline.receiver;

import lombok.extern.slf4j.Slf4j;
import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.publisher.PublisherTask;

import java.util.*;

@Slf4j
public class DefaultPublisherPipelineReceiver<T extends ConversationItem> extends AbstractPipelineReceiver<T, PublisherTask<T>> {
    public static final EnumMap<Code, String> CODES = new EnumMap<Code, String>(Code.class){{
        put(Code.ALREADY_BLOCKED_OUT, "default-publisher-receiver.already-blocked-out");
        put(Code.ALREADY_BLOCKED, "default-publisher-receiver.already-blocked");
        put(Code.ALREADY_SUBSCRIBED, "default-publisher-receiver.already-subscribed");
        put(Code.ALREADY_UNSUBSCRIBED, "default-publisher-receiver.already-unsubscribed");
        put(Code.IS_BLOCKED, "default-publisher-receiver.is-blocked");
        put(Code.INVALID_SESSION_ID, "default-publisher-receiver.invalid-session-id");
        put(Code.NO_ONE_SUBSCRIBER, "default-publisher-receiver.no-one-subscriber");
        put(Code.SUBSCRIBER_FAIL, "default-publisher-receiver.subscriber-fail");
    }};

    @Override
    protected String getCode(final Code code) {
        return CODES.get(code);
    }
}