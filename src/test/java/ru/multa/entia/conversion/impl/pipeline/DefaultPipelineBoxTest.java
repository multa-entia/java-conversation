package ru.multa.entia.conversion.impl.pipeline;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: 18.11.2023 faked bool
class DefaultPipelineBoxTest {

    @Test
    void shouldCheckValueGetting_ifNull() {
        DefaultPipelineBox<String> box = new DefaultPipelineBox<>(null);

        assertThat(box.value()).isNull();
    }

    @Test
    void shouldCheckValueGetting() {
        String expected = Faker.str_().random();
        DefaultPipelineBox<String> box = new DefaultPipelineBox<>(expected);

        assertThat(box.value()).isEqualTo(expected);
    }
}