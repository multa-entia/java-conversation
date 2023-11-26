package ru.multa.entia.conversion.impl.pipeline;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;

import static org.assertj.core.api.Assertions.assertThat;

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