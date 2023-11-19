package ru.multa.entia.conversion.impl.type;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.seed.Seed;
import utils.ResultUtil;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: 18.11.2023 faked bool
class DefaultTypeValueCheckerTest {

    @Test
    void shouldCheckChecking_ifInstanceNull() {
        Seed seed = new DefaultTypeValueChecker().check(null);

        assertThat(ResultUtil.isEqual(seed, ResultUtil.seed(DefaultTypeValueChecker.Code.IS_NULL.getValue()))).isTrue();
    }

    @Test
    void shouldCheckChecking_ifNotStr() {
        Seed seed = new DefaultTypeValueChecker().check(Faker.uuid_().random());

        assertThat(ResultUtil.isEqual(seed, ResultUtil.seed(DefaultTypeValueChecker.Code.IS_NOT_STR.getValue()))).isTrue();
    }

    @Test
    void shouldCheckSuccessChecking() {
        Seed seed = new DefaultTypeValueChecker().check(Faker.str_().random());

        assertThat(seed).isNull();
    }
}