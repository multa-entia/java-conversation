package ru.multa.entia.conversion.impl.content;


import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.value.Value;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultContentSerializerTest {

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    @Test
    void shouldCheckSerialization_ifBadAccess() {
        Result<String> result = new DefaultContentSerializer().apply(new BadAccessTestValue());

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(CR.get(DefaultContentSerializer.Code.BAD_ACCESS))
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckSerialization() {
        Integer intValue = Faker.int_().random(0, 10);
        String expectedValue = String.format("{\"x\":%s}", intValue);

        Result<String> result = new DefaultContentSerializer().apply(new TestValue(intValue));

        assertThat(Results.comparator(result)
                .isSuccess()
                .value(expectedValue)
                .compare()).isTrue();
    }

    private static class BadAccessTestValue implements Value {}
    public record TestValue(int x) implements Value {}
}