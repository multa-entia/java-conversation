package ru.multa.entia.conversion.impl.address;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.utils.Results;
import utils.ResultUtil;
import utils.TestAddress;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultAddressFactoryTest {
    @Test
    void shouldCheckCreation_checkerFail() {
        String expectedCode = Faker.str_().random();
        Checker<Object> checker = object -> {
            return ResultUtil.seed(expectedCode);
        };

        Result<Address> result = new DefaultAddressFactory(checker, null).create(null);

        assertThat(Results.comparator(result).isFail().seedsComparator().code(expectedCode).back().compare()).isTrue();
    }

    @Test
    void shouldCheckCreation_checkerSuccess() {
        String expectedValue = Faker.str_().random();
        Checker<Object> checker =  object -> {return null;};

        Result<Address> result = new DefaultAddressFactory(checker, TestAddress::new).create(expectedValue);

        assertThat(Results.comparator(result).isSuccess().value(new TestAddress(expectedValue)).compare()).isTrue();
    }
}