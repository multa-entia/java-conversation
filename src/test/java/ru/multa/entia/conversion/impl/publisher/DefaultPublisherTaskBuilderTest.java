package ru.multa.entia.conversion.impl.publisher;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.conversion.api.holder.HolderReleaseStrategy;
import ru.multa.entia.conversion.api.holder.HolderTimeoutStrategy;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.conversion.api.publisher.PublisherTaskBuilder;
import ru.multa.entia.conversion.api.publisher.PublisherTaskCreator;
import utils.FakerUtil;
import utils.TestHolderReleaseStrategy;
import utils.TestHolderTimeoutStrategy;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPublisherTaskBuilderTest {

    private static final PublisherTaskCreator<Message> CREATOR = (item, timeoutStrategy, releaseStrategy) -> {
        TestPublisherTask task = Mockito.mock(TestPublisherTask.class);
        Mockito.when(task.item()).thenReturn(item);
        Mockito.when(task.timeoutStrategy()).thenReturn(timeoutStrategy);
        Mockito.when(task.releaseStrategy()).thenReturn(releaseStrategy);

        return task;
    };

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

    @SneakyThrows
    @Test
    void shouldCheckStrategiesUsage_strategy_doNotUse() {
        DefaultPublisherTaskBuilder<Message> expectedBuilder = new DefaultPublisherTaskBuilder<>();
        PublisherTaskBuilder<Message> builder = expectedBuilder.doNotUseStrategy();

        assertThat(builder).isEqualTo(expectedBuilder);

        Field field = builder.getClass().getDeclaredField("strategiesUsage");
        field.setAccessible(true);
        DefaultPublisherTaskBuilder.StrategiesUsage strategiesUsage
                = (DefaultPublisherTaskBuilder.StrategiesUsage) field.get(builder);
        assertThat(strategiesUsage).isEqualTo(DefaultPublisherTaskBuilder.StrategiesUsage.DO_NOT_USE);
    }

    @SneakyThrows
    @Test
    void shouldCheckStrategiesUsage_strategy_useDefault() {
        DefaultPublisherTaskBuilder<Message> expectedBuilder = new DefaultPublisherTaskBuilder<>();
        PublisherTaskBuilder<Message> builder = expectedBuilder.useDefaultStrategy();

        assertThat(builder).isEqualTo(expectedBuilder);

        Field field = builder.getClass().getDeclaredField("strategiesUsage");
        field.setAccessible(true);
        DefaultPublisherTaskBuilder.StrategiesUsage strategiesUsage
                = (DefaultPublisherTaskBuilder.StrategiesUsage) field.get(builder);
        assertThat(strategiesUsage).isEqualTo(DefaultPublisherTaskBuilder.StrategiesUsage.USE_DEFAULT);
    }

    @SneakyThrows
    @Test
    void shouldCheckStrategiesUsage_strategy_useSet() {
        DefaultPublisherTaskBuilder<Message> expectedBuilder = new DefaultPublisherTaskBuilder<>();
        PublisherTaskBuilder<Message> builder = expectedBuilder.useDefaultStrategy().useSetStrategy();

        assertThat(builder).isEqualTo(expectedBuilder);

        Field field = builder.getClass().getDeclaredField("strategiesUsage");
        field.setAccessible(true);
        DefaultPublisherTaskBuilder.StrategiesUsage strategiesUsage
                = (DefaultPublisherTaskBuilder.StrategiesUsage) field.get(builder);
        assertThat(strategiesUsage).isEqualTo(DefaultPublisherTaskBuilder.StrategiesUsage.USE_SET);
    }

    @Test
    void shouldCheckBuilding_strategyNotSet() {
        Message expectedItem = FakerUtil.randomMessage();
        TestHolderTimeoutStrategy expectedTimeoutStrategy = new TestHolderTimeoutStrategy();
        TestHolderReleaseStrategy expectedReleaseStrategy = new TestHolderReleaseStrategy();
        PublisherTask<Message> task = new DefaultPublisherTaskBuilder<>(
                () -> {return null;},
                () -> {return null;},
                CREATOR
        )
                .item(expectedItem)
                .timeoutStrategy(expectedTimeoutStrategy)
                .releaseStrategy(expectedReleaseStrategy)
                .build();

        assertThat(task.item()).isEqualTo(expectedItem);
        assertThat(task.timeoutStrategy()).isEqualTo(expectedTimeoutStrategy);
        assertThat(task.releaseStrategy()).isEqualTo(expectedReleaseStrategy);
    }

    @Test
    void shouldCheckBuilding_strategy_doNotUse() {
        Message expectedItem = FakerUtil.randomMessage();
        PublisherTask<Message> task = new DefaultPublisherTaskBuilder<>(
                TestHolderTimeoutStrategy::new,
                TestHolderReleaseStrategy::new,
                CREATOR
        )
                .item(expectedItem)
                .doNotUseStrategy()
                .build();

        assertThat(task.item()).isEqualTo(expectedItem);
        assertThat(task.timeoutStrategy()).isNull();
        assertThat(task.releaseStrategy()).isNull();
    }

    @Test
    void shouldCheckBuilding_strategy_useDefault() {
        Message expectedItem = FakerUtil.randomMessage();
        TestHolderTimeoutStrategy expectedTimeoutStrategy = new TestHolderTimeoutStrategy();
        TestHolderReleaseStrategy expectedReleaseStrategy = new TestHolderReleaseStrategy();
        PublisherTask<Message> task = new DefaultPublisherTaskBuilder<>(
                () -> {return expectedTimeoutStrategy;},
                () -> {return expectedReleaseStrategy;},
                CREATOR
        )
                .item(expectedItem)
                .useDefaultStrategy()
                .build();

        assertThat(task.item()).isEqualTo(expectedItem);
        assertThat(task.timeoutStrategy()).isEqualTo(expectedTimeoutStrategy);
        assertThat(task.releaseStrategy()).isEqualTo(expectedReleaseStrategy);
    }

    @Test
    void shouldCheckBuilding_strategy_useSet() {
        Message expectedItem = FakerUtil.randomMessage();
        TestHolderTimeoutStrategy expectedTimeoutStrategy = new TestHolderTimeoutStrategy();
        TestHolderReleaseStrategy expectedReleaseStrategy = new TestHolderReleaseStrategy();
        PublisherTask<Message> task = new DefaultPublisherTaskBuilder<>(
                () -> {return null;},
                () -> {return null;},
                CREATOR
        )
                .item(expectedItem)
                .useSetStrategy()
                .timeoutStrategy(expectedTimeoutStrategy)
                .releaseStrategy(expectedReleaseStrategy)
                .build();

        assertThat(task.item()).isEqualTo(expectedItem);
        assertThat(task.timeoutStrategy()).isEqualTo(expectedTimeoutStrategy);
        assertThat(task.releaseStrategy()).isEqualTo(expectedReleaseStrategy);
    }

    private interface TestPublisherTask extends PublisherTask<Message> {}
}