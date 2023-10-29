package ru.multa.entia.conversion.impl.content;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.fakers.impl.Faker;
import utils.TestType;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: 29.10.2023 ME-16
class DefaultContentCreatorTest {

    @Test
    void shouldCheckCreation() {
        TestType expectedType = new TestType(Faker.str_().random());
        String expectedValue = Faker.str_().random();
        Content content = new DefaultContentCreator().create(expectedType, expectedValue);

        assertThat(content.type()).isEqualTo(expectedType);
        assertThat(content.value()).isEqualTo(expectedValue);
    }
}