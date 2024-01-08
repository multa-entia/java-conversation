package ru.multa.entia.conversion.impl.holder;

import ru.multa.entia.conversion.api.confirmation.Confirmation;
import ru.multa.entia.conversion.api.holder.*;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultHolder implements Holder {
    public enum Code {
        MESSAGE_IS_NULL,
        MESSAGE_ALREADY_CONTAINED,
        STORAGE_IS_FULL,
        CONFIRMATION_IS_NULL,
        CONFIRMATION_HAS_BAD_ID;
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.MESSAGE_IS_NULL, "conversation:holder.default:message-is-null");
        CR.update(Code.MESSAGE_ALREADY_CONTAINED, "conversation:holder.default:message-already-contained");
        CR.update(Code.STORAGE_IS_FULL, "conversation:holder.default:storage-is-full");
        CR.update(Code.CONFIRMATION_IS_NULL, "conversation:holder.default:confirmation-is-null");
        CR.update(Code.CONFIRMATION_HAS_BAD_ID, "conversation:holder.default:confirmation-has-bad-id");
    }

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock rLock = lock.readLock();
    private final Lock wLock = lock.writeLock();

    private final static int DEFAULT_POOL_SIZE = 8;
    private final static int EXCLUDED_MIN_THRESHOLD = 0;
    private final static int DEFAULT_SIZE = 100_000;

    private final int size;
    private final HolderItemCreator creator;
    private final Map<UUID, HolderItem> storage = new HashMap<>();

    private final ScheduledExecutorService scheduleService;

    public DefaultHolder() {
        this(EXCLUDED_MIN_THRESHOLD, null, null);
    }

    public DefaultHolder(final int size) {
        this(size, null, null);
    }

    public DefaultHolder(final HolderItemCreator creator) {
        this(EXCLUDED_MIN_THRESHOLD, creator, null);
    }

    public DefaultHolder(final ScheduledExecutorService scheduleService){
        this(EXCLUDED_MIN_THRESHOLD, null, scheduleService);
    }

    public DefaultHolder(final int size, final HolderItemCreator creator, final ScheduledExecutorService scheduleService) {
        this.size = size > EXCLUDED_MIN_THRESHOLD ? size : DEFAULT_SIZE;
        this.creator = Objects.requireNonNullElse(creator, new DefaultHolderItemCreator());
        this.scheduleService = Objects.requireNonNullElse(
                scheduleService,
                Executors.newScheduledThreadPool(DEFAULT_POOL_SIZE));
    }

    @Override
    public Result<HolderItem> hold(final Message message) {
        return hold(message, null, null);
    }

    @Override
    public Result<HolderItem> hold(final Message message,
                                   final HolderTimeoutStrategy timeoutStrategy,
                                   final HolderReleaseStrategy releaseStrategy) {
        HolderItem item = null;
        Code code = null;
        if (message == null){
            code = Code.MESSAGE_IS_NULL;
        } else {
            UUID id = message.id();
            item = creator.create(message, timeoutStrategy, releaseStrategy);
            wLock.lock();
                if (storage.containsKey(id)){
                    code = Code.MESSAGE_ALREADY_CONTAINED;
                    wLock.unlock();
                } else if (storage.size() >= size) {
                    code = Code.STORAGE_IS_FULL;
                    wLock.unlock();
                } else {
                    storage.put(message.id(), item);
                    wLock.unlock();

                    addScheduler(item);
                }
        }

        return code != null
                ? DefaultResultBuilder.<HolderItem>fail(CR.get(code))
                : DefaultResultBuilder.<HolderItem>ok(item);
    }

    @Override
    public Result<HolderItem> release(final Confirmation confirmation) {
        HolderItem item = null;
        Code code = null;
        if (confirmation == null){
            code = Code.CONFIRMATION_IS_NULL;
        } else {
            UUID id = confirmation.id();
            wLock.lock();
                if (storage.containsKey(id)){
                    item = storage.remove(id);
                    wLock.unlock();
                    if (item != null && item.releaseStrategy() != null){
                        item.releaseStrategy().execute(item.message(), confirmation);
                    }
                } else {
                    wLock.unlock();
                    code = Code.CONFIRMATION_HAS_BAD_ID;
                }
        }

        return code != null
                ? DefaultResultBuilder.<HolderItem>fail(CR.get(code))
                : DefaultResultBuilder.<HolderItem>ok(item);
    }

    private void addScheduler(final HolderItem item) {
        Message message = item.message();
        UUID uuid = new UUID(message.id().getMostSignificantBits(), message.id().getLeastSignificantBits());
        if (item.timeoutStrategy() != null){
            HolderTimeoutStrategy strategy = item.timeoutStrategy();
            scheduleService.schedule(() -> {
                Message gottenMessage = getOrNull(uuid);
                if (gottenMessage != null){
                    strategy.execute(gottenMessage);
                }
            }, strategy.getTimeout(), strategy.getTimeUnit());
        }
    }

    private Message getOrNull(final UUID id){
        rLock.lock();
            Message message = storage.containsKey(id) ? storage.get(id).message() : null;
        rLock.unlock();
        return message;
    }
}
