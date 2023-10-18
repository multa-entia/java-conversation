package ru.multa.entia.conversion.impl.holder;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.holder.HolderItem;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.results.api.result.Result;
import utils.FakerUtil;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

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

    void shouldCheckRelease_ifConfirmationIsNull() {

    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckRelease() {
//        Message firstMessage = FakerUtil.randomMessage();
//        Message secondMessage = FakerUtil.randomMessage();
//        DefaultHolder holder = new DefaultHolder();
//        holder.hold(firstMessage, null, null);
//        holder.hold(secondMessage, null, null);
//        Result<HolderItem> result = holder.release(FakerUtil.confirmation(firstMessage));
//
//        assertThat(result.ok()).isTrue();
//        assertThat(result.seed()).isNull();
//
//        HolderItem item = result.value();
//        assertThat(item.message()).isEqualTo(secondMessage);
//        assertThat(item.timeoutStrategy()).isNull();
//        assertThat(item.releaseStrategy()).isNull();
//
//        Field field = holder.getClass().getDeclaredField("storage");
//        field.setAccessible(true);
//        Map<UUID, HolderItem> storage = (Map<UUID, HolderItem>) field.get(holder);
//        assertThat(storage).hasSize(1);
//        assertThat(storage).containsKey(firstMessage.id());
//
//        HolderItem storageItem = storage.get(firstMessage.id());
//        assertThat(storageItem.message()).isEqualTo(firstMessage);
//        assertThat(storageItem.timeoutStrategy()).isNull();
//        assertThat(storageItem.releaseStrategy()).isNull();
    }

    @Test
    void shouldCheckTimeoutStrategyExecution() {

    }

    @Test
    void shouldCheckReleaseStrategyExecution() {

    }
}