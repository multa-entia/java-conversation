package ru.multa.entia.conversion.impl.publisher;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.holder.HolderReleaseStrategy;
import ru.multa.entia.conversion.api.holder.HolderTimeoutStrategy;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.publisher.PublisherTaskBuilder;
import utils.FakerUtil;
import utils.TestHolderReleaseStrategy;
import utils.TestHolderTimeoutStrategy;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPublisherTaskBuilderTest {
    @SneakyThrows
    @Test
    void shouldCheckItemSetting() {
        DefaultPublisherTaskBuilder<Message> expectedBuilder = new DefaultPublisherTaskBuilder<>();
        Message expectedMessage = FakerUtil.randomMessage();
        PublisherTaskBuilder<Message> builder = expectedBuilder.item(expectedMessage);

        assertThat(builder).isEqualTo(expectedBuilder);

        Field field = builder.getClass().getDeclaredField("item");
        field.setAccessible(true);
        Message message = (Message) field.get(builder);
        assertThat(message).isEqualTo(expectedMessage);
    }

    @SneakyThrows
    @Test
    void shouldCheckTimeoutStrategySetting() {
        DefaultPublisherTaskBuilder<Message> expectedBuilder = new DefaultPublisherTaskBuilder<>();
        TestHolderTimeoutStrategy expectedStrategy = new TestHolderTimeoutStrategy();
        PublisherTaskBuilder<Message> builder = expectedBuilder.timeoutStrategy(expectedStrategy);

        assertThat(builder).isEqualTo(expectedBuilder);

        Field field = builder.getClass().getDeclaredField("timeoutStrategy");
        field.setAccessible(true);
        HolderTimeoutStrategy strategy = (HolderTimeoutStrategy) field.get(builder);
        assertThat(strategy).isEqualTo(expectedStrategy);
    }

    @SneakyThrows
    @Test
    void shouldCheckReleaseTimeoutSetting() {
        DefaultPublisherTaskBuilder<Message> expectedBuilder = new DefaultPublisherTaskBuilder<>();
        TestHolderReleaseStrategy expectedStrategy = new TestHolderReleaseStrategy();
        PublisherTaskBuilder<Message> builder = expectedBuilder.releaseStrategy(expectedStrategy);

        assertThat(builder).isEqualTo(expectedBuilder);

        Field field = builder.getClass().getDeclaredField("releaseStrategy");
        field.setAccessible(true);
        HolderReleaseStrategy strategy = (HolderReleaseStrategy) field.get(builder);
        assertThat(strategy).isEqualTo(expectedStrategy);
    }

    @Test
    void shouldCheckStrategiesUsage_doNotUseStrategy() {

    }

    @Test
    void shouldCheckStrategiesUsage_useDefaultStrategy() {

    }

    @Test
    void shouldCheckStrategiesUsage_useSetStrategy() {

    }

    @Test
    void shouldCheckBuilding() {

    }

    @Test
    void shouldCheckBuildingAndPublishing_ifServiceIsNull() {

    }

    @Test
    void shouldCheckBuildingAndPublishing_ifServiceReturnsBadResult() {

    }

    @Test
    void shouldCheckBuildingAndPublishing() {

    }
}