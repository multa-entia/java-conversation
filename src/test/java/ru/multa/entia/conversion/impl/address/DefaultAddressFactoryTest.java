package ru.multa.entia.conversion.impl.address;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import utils.ResultUtil;
import utils.TestAddress;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: 18.11.2023 faked bool
class DefaultAddressFactoryTest {
    @Test
    void shouldCheckCreation_checkerFail() {
        String expectedCode = Faker.str_().random();
        Checker<Object> checker = object -> {
            return ResultUtil.seed(expectedCode);
        };

        Result<Address> result = new DefaultAddressFactory(checker, null).create(null);

        assertThat(ResultUtil.isEqual(result, ResultUtil.fail(expectedCode))).isTrue();
    }

    @Test
    void shouldCheckCreation_checkerSuccess() {
        String expectedValue = Faker.str_().random();
        Checker<Object> checker =  object -> {return null;};

        Result<Address> result = new DefaultAddressFactory(checker, TestAddress::new).create(expectedValue);

        assertThat(ResultUtil.isEqual(result, ResultUtil.ok(new TestAddress(expectedValue)))).isTrue();
    }
}