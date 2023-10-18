package ru.multa.entia.conversion.impl.holder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.confirmation.Confirmation;
import ru.multa.entia.conversion.api.holder.*;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DefaultHolder implements Holder {
    @RequiredArgsConstructor
    @Getter
    public enum Code{
        MESSAGE_IS_NULL("default-holder.message-is-null"),
        MESSAGE_ALREADY_CONTAINED("default-holder.message-already-contained"),
        STORAGE_IS_FULL("default-holder.storage-is-full");

        private final String value;
    }

    private final static int EXCLUDED_MIN_THRESHOLD = 0;
    private final static int DEFAULT_SIZE = 100_000;

    private final int size;
    private final HolderItemCreator creator;
    private final Map<UUID, HolderItem> storage = new HashMap<>();

    public DefaultHolder() {
        this(EXCLUDED_MIN_THRESHOLD, null);
    }

    public DefaultHolder(final int size) {
        this(size, null);
    }

    public DefaultHolder(final HolderItemCreator creator) {
        this(EXCLUDED_MIN_THRESHOLD, creator);
    }

    public DefaultHolder(final int size, final HolderItemCreator creator) {
        this.size = size > EXCLUDED_MIN_THRESHOLD ? size : DEFAULT_SIZE;
        this.creator = creator == null ? new DefaultHolderItemCreator() : creator;
    }

    // TODO: 17.10.2023 remake sync-block
    @Override
    public synchronized Result<HolderItem> hold(final Message message) {
        return hold(message, null, null);
    }

    // TODO: 17.10.2023 remake sync-block
    @Override
    public synchronized Result<HolderItem> hold(final Message message,
                                                final HolderTimeoutStrategy timeoutStrategy,
                                                final HolderReleaseStrategy releaseStrategy) {
        // TODO: 18.10.2023 make refact.
        if (message == null){
            return DefaultResultBuilder.<HolderItem>fail(Code.MESSAGE_IS_NULL.getValue());
        } else {
            if (storage.containsKey(message.id())){
                return DefaultResultBuilder.<HolderItem>fail(Code.MESSAGE_ALREADY_CONTAINED.getValue());
            } else {
                if (storage.size() >= size){
                    return DefaultResultBuilder.<HolderItem>fail(Code.STORAGE_IS_FULL.getValue());
                } else {
                    // TODO: 18.10.2023 move to sep. method
                    HolderItem item = creator.create(message, timeoutStrategy, releaseStrategy);
                    storage.put(message.id(), item);

                    return DefaultResultBuilder.<HolderItem>ok(item);
                }
            }
        }

        // TODO: 18.10.2023 ???
//        return null;
    }

    // TODO: 17.10.2023 remake sync-block
    @Override
    public synchronized Result<HolderItem> release(Confirmation confirmation) {
        return null;
    }
}
