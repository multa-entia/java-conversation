package ru.multa.entia.conversion.impl.holder;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.holder.HolderItem;
import ru.multa.entia.fakers.impl.Faker;
import utils.*;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultHolderItemCreatorTest {

    @Test
    void shouldCheckCreation() {
        TestMessage expectedMessage = new TestMessage(
                Faker.uuid_().random(),
                Faker.uuid_().random(),
                new TestAddress(Faker.str_().random()),
                new TestAddress(Faker.str_().random()),
                // TODO: 18.10.2023 use faker
                false,
                new TestContent(new TestType(Faker.str_().random()), Faker.str_().random())
        );
        TestHolderTimeoutStrategy expectedTimeoutStrategy = new TestHolderTimeoutStrategy();
        TestHolderReleaseStrategy expectedReleaseStrategy = new TestHolderReleaseStrategy();

        HolderItem item = new DefaultHolderItemCreator()
                .create(expectedMessage, expectedTimeoutStrategy, expectedReleaseStrategy);

        assertThat(item.message()).isEqualTo(expectedMessage);
        assertThat(item.timeoutStrategy()).isEqualTo(expectedTimeoutStrategy);
        assertThat(item.releaseStrategy()).isEqualTo(expectedReleaseStrategy);
    }
}