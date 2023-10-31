package ru.multa.entia.conversion.impl.message;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.seed.Seed;
import utils.ResultUtil;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultMessageCheckerTest {

    @Test
    void shouldCheckChecking_ifInstanceNull() {
        Seed seed = new DefaultMessageChecker().check(null);

        assertThat(ResultUtil.isEqual(seed, ResultUtil.seed(DefaultMessageChecker.Code.IS_NULL.getValue()))).isTrue();
    }

    @Test
    void shouldCheckChecking() {
        Seed seed = new DefaultMessageChecker().check(Faker.str_().random());

        assertThat(seed).isNull();
    }
}