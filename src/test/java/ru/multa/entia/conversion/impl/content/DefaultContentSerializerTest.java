package ru.multa.entia.conversion.impl.content;


import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.value.Value;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import utils.ResultUtil;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultContentSerializerTest {

    @Test
    void shouldCheckSerialization_ifBadAccess() {
        Result<String> result = new DefaultContentSerializer().apply(new BadAccessTestValue());

        assertThat(ResultUtil.isEqual(result, ResultUtil.fail(DefaultContentSerializer.Code.BAD_ACCESS.getValue()))).isTrue();
    }

    @Test
    void shouldCheckSerialization() {
        Integer intValue = Faker.int_().random(0, 10);
        String expectedValue = String.format("{\"x\":%s}", intValue);

        Result<String> result = new DefaultContentSerializer().apply(new TestValue(intValue));

        assertThat(ResultUtil.isEqual(result, ResultUtil.ok(expectedValue))).isTrue();
    }

    private static class BadAccessTestValue implements Value {}
    public record TestValue(int x) implements Value {}
}