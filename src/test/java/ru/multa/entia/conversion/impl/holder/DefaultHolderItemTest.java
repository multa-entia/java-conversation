package ru.multa.entia.conversion.impl.holder;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import utils.*;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultHolderItemTest {

    @Test
    void shouldCheckMessageGetting_ifNull() {
        DefaultHolderItem item = new DefaultHolderItem(null, null, null);

        assertThat(item.message()).isNull();
    }

    @Test
    void shouldCheckMessageGetting() {
        TestMessage expectedMessage = new TestMessage(
                Faker.uuid_().random(),
                Faker.uuid_().random(),
                new TestAddress(Faker.str_().random()),
                new TestAddress(Faker.str_().random()),
                // TODO: 18.10.2023 use faker
                false,
                new TestContent(new TestType(Faker.str_().random()), Faker.str_().random())
        );
        DefaultHolderItem item = new DefaultHolderItem(expectedMessage, null, null);

        assertThat(item.message()).isEqualTo(expectedMessage);
    }

    @Test
    void shouldCheckTimeoutStrategyGetting_ifNull() {
        DefaultHolderItem item = new DefaultHolderItem(null, null, null);

        assertThat(item.timeoutStrategy()).isNull();
    }

    @Test
    void shouldCheckTimeoutStrategyGetting() {
        TestHolderTimeoutStrategy expectedStrategy = new TestHolderTimeoutStrategy();
        DefaultHolderItem item = new DefaultHolderItem(null, expectedStrategy, null);

        assertThat(item.timeoutStrategy()).isEqualTo(expectedStrategy);
    }

    @Test
    void shouldCheckReleaseStrategyGetting_ifNull() {
        DefaultHolderItem item = new DefaultHolderItem(null, null, null);

        assertThat(item.releaseStrategy()).isNull();
    }

    @Test
    void shouldCheckReleaseStrategyGetting() {
        TestHolderReleaseStrategy expectedStrategy = new TestHolderReleaseStrategy();
        DefaultHolderItem item = new DefaultHolderItem(null, null, expectedStrategy);

        assertThat(item.releaseStrategy()).isEqualTo(expectedStrategy);
    }
}