package ru.multa.entia.conversion.impl.holder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.confirmation.Confirmation;
import ru.multa.entia.conversion.api.holder.HolderItem;
import ru.multa.entia.conversion.api.holder.HolderReleaseStrategy;
import ru.multa.entia.conversion.api.holder.HolderTimeoutStrategy;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.results.api.result.Result;
import utils.FakerUtil;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultHolderTest {

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckAddition() {
        Message expectedMessage = FakerUtil.randomMessage();

        DefaultHolder holder = new DefaultHolder();
        Result<HolderItem> result = holder.hold(expectedMessage, null, null);

        assertThat(result.ok()).isTrue();
        assertThat(result.seed()).isNull();

        HolderItem item = result.value();
        assertThat(item.message()).isEqualTo(expectedMessage);
        assertThat(item.timeoutStrategy()).isNull();
        assertThat(item.releaseStrategy()).isNull();

        Field field = holder.getClass().getDeclaredField("storage");
        field.setAccessible(true);
        Map<UUID, HolderItem> storage = (Map<UUID, HolderItem>) field.get(holder);
        assertThat(storage).hasSize(1);
        assertThat(storage).containsKey(expectedMessage.id());

        HolderItem storageItem = storage.get(expectedMessage.id());
        assertThat(storageItem.message()).isEqualTo(expectedMessage);
        assertThat(storageItem.timeoutStrategy()).isNull();
        assertThat(storageItem.releaseStrategy()).isNull();
    }

    @Test
    void shouldCheckAddition_ifMessageNull() {
        DefaultHolder holder = new DefaultHolder();
        Result<HolderItem> result = holder.hold(null, null, null);

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(DefaultHolder.Code.MESSAGE_IS_NULL.getValue());
        assertThat(result.seed().args()).isEmpty();
    }

    @Test
    void shouldCheckAddition_ifMessageAlreadyHolt() {
        Message message = FakerUtil.randomMessage();
        DefaultHolder holder = new DefaultHolder();
        holder.hold(message, null, null);
        Result<HolderItem> result = holder.hold(message, null, null);

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(DefaultHolder.Code.MESSAGE_ALREADY_CONTAINED.getValue());
        assertThat(result.seed().args()).isEmpty();
    }

    @Test
    void shouldCheckAddition_ifStorageIsFull() {
        DefaultHolder holder = new DefaultHolder(1);
        holder.hold(FakerUtil.randomMessage(), null, null);
        Result<HolderItem> result = holder.hold(FakerUtil.randomMessage(), null, null);

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(DefaultHolder.Code.STORAGE_IS_FULL.getValue());
        assertThat(result.seed().args()).isEmpty();
    }

    @Test
    void shouldCheckRelease_ifConfirmationIsNull() {
        DefaultHolder holder = new DefaultHolder();
        holder.hold(FakerUtil.randomMessage(), null, null);
        Result<HolderItem> result = holder.release(null);

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(DefaultHolder.Code.CONFIRMATION_IS_NULL.getValue());
        assertThat(result.seed().args()).isEmpty();
    }

    @Test
    void shouldCheckRelease_ifConfirmationContainsBadId() {
        DefaultHolder holder = new DefaultHolder();
        Message message = FakerUtil.randomMessage();
        holder.hold(message, null, null);

        Confirmation confirmation = null;
        for (int i = 0; i < 10; i++) {
            confirmation = FakerUtil.randomConfirm();
            if (confirmation.id() != message.id()){
                break;
            } else {
                confirmation = null;
            }
        }
        assertThat(confirmation).isNotNull();

        Result<HolderItem> result = holder.release(confirmation);

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(DefaultHolder.Code.CONFIRMATION_HAS_BAD_ID.getValue());
        assertThat(result.seed().args()).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckRelease() {
        Message firstMessage = FakerUtil.randomMessage();
        Message secondMessage = FakerUtil.randomMessage();
        DefaultHolder holder = new DefaultHolder();
        holder.hold(firstMessage, null, null);
        holder.hold(secondMessage, null, null);
        Result<HolderItem> result = holder.release(FakerUtil.confirmation(firstMessage));

        assertThat(result.ok()).isTrue();
        assertThat(result.seed()).isNull();

        HolderItem item = result.value();
        assertThat(item.message()).isEqualTo(firstMessage);
        assertThat(item.timeoutStrategy()).isNull();
        assertThat(item.releaseStrategy()).isNull();

        Field field = holder.getClass().getDeclaredField("storage");
        field.setAccessible(true);
        Map<UUID, HolderItem> storage = (Map<UUID, HolderItem>) field.get(holder);
        assertThat(storage).hasSize(1);
        assertThat(storage).containsKey(secondMessage.id());

        HolderItem storageItem = storage.get(secondMessage.id());
        assertThat(storageItem.message()).isEqualTo(secondMessage);
        assertThat(storageItem.timeoutStrategy()).isNull();
        assertThat(storageItem.releaseStrategy()).isNull();
    }

    @SneakyThrows
    @Test
    void shouldCheckTimeoutStrategyExecution() {
        Message expectedMessage = FakerUtil.randomMessage();
        DefaultHolder holder = new DefaultHolder();
        int strategyTimeout = 100;
        int threadTimeout = strategyTimeout * 2;
        int testTimeout = strategyTimeout * 3;

        TestHolderTimeoutStrategy strategy = new TestHolderTimeoutStrategy(strategyTimeout, TimeUnit.MILLISECONDS);
        Thread thread = new Thread(() -> {
            holder.hold(expectedMessage, strategy, null);
            try {
                Thread.sleep(threadTimeout);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        Thread.sleep(testTimeout);

        assertThat(strategy.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void shouldCheckReleaseStrategyExecution() {
        Message expectedMessage = FakerUtil.randomMessage();
        Confirmation expectedConfirmation = FakerUtil.confirmation(expectedMessage);
        TestHolderReleaseStrategy strategy = new TestHolderReleaseStrategy();
        DefaultHolder holder = new DefaultHolder();
        holder.hold(expectedMessage, null, strategy);
        holder.release(expectedConfirmation);

        assertThat(strategy.getMessage()).isEqualTo(expectedMessage);
        assertThat(strategy.getConfirmation()).isEqualTo(expectedConfirmation);
    }

    @RequiredArgsConstructor
    @Getter
    private static class TestHolderTimeoutStrategy implements HolderTimeoutStrategy {
        private final int timeout;
        private final TimeUnit timeUnit;

        private Message message;

        @Override
        public void execute(final Message message) {
            this.message = message;
        }
    }

    @Getter
    private static class TestHolderReleaseStrategy implements HolderReleaseStrategy {
        private Message message;
        private Confirmation confirmation;

        @Override
        public void execute(final Message message, final Confirmation confirmation) {
            this.message = message;
            this.confirmation = confirmation;
        }
    }
}