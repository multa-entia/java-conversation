package ru.multa.entia.conversion.impl.type;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.conversion.api.type.Type;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.utils.Results;
import utils.ResultUtil;
import utils.TestType;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTypeFactoryTest {
    @Test
    void shouldCheckCreation_ifFailChecking() {
        String expectedCode = Faker.str_().random();
        Checker<Object> checker = instance -> {
            return ResultUtil.seed(expectedCode);
        };

        DefaultTypeFactory factory = new DefaultTypeFactory(checker, null);

        Result<Type> result = factory.create(null);

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(expectedCode)
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckCreation() {
        String expectedValue = Faker.str_().random(5, 10);
        Checker<Object> checker = instance -> {return null;};

        Result<Type> result = new DefaultTypeFactory(checker, TestType::new).create(expectedValue);

        assertThat(Results.comparator(result).isSuccess().seedsComparator().isNull().back().compare()).isTrue();
        assertThat(result.value().value()).isEqualTo(expectedValue);
    }
}