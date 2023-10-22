package ru.multa.entia.conversion.impl.publisher;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.publisher.PublisherTaskBuilder;
import utils.FakerUtil;

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

    @Test
    void shouldCheckTimeoutStrategySetting() {

    }

    @Test
    void shouldCheckReleaseTimeoutSetting() {

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