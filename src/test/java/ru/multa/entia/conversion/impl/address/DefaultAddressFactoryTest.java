package ru.multa.entia.conversion.impl.address;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import utils.TestAddress;
import utils.TestSeed;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultAddressFactoryTest {
    @Test
    void shouldCheckCreation_checkerFail() {
        String expectedCode = Faker.str_().random();
        Checker<Object> checker = object -> {
            return new TestSeed(expectedCode, new Object[0]);
        };

        Result<Address> result = new DefaultAddressFactory(checker, null).create(null);

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(expectedCode);
        assertThat(result.seed().args()).isEmpty();
    }

    @Test
    void shouldCheckCreation_checkerSuccess() {
        String expectedValue = Faker.str_().random();
        Checker<Object> checker =  object -> {return null;};

        Result<Address> result = new DefaultAddressFactory(checker, TestAddress::new).create(expectedValue);

        assertThat(result.ok()).isTrue();
        assertThat(result.value().value()).isEqualTo(expectedValue);
        assertThat(result.seed()).isNull();
    }
}