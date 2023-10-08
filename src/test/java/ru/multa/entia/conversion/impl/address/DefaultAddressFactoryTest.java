package ru.multa.entia.conversion.impl.address;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultAddressFactoryTest {

    //    @Test
//    void shouldCheckCreation_withDefaultChecker_ifNull() {
//        Result<Address> result = new DefaultAddressFactory().create(null);
//
//        assertThat(result.ok()).isFalse();
//        assertThat(result.value()).isNull();
//        Seed seed = result.seed();
//        assertThat(seed.code()).isEqualTo(DefaultAddressFactory.Code.INSTANCE_IS_NULL.getValue());
//        assertThat(seed.args()).isEmpty();
//    }

    // TODO: 08.10.2023 del
//    @Test
//    void shouldCheckCreation_ifNotString() {
//        Result<Address> result = new DefaultAddressFactory().create(Faker.int_().random());
//
//        assertThat(result.ok()).isFalse();
//        assertThat(result.value()).isNull();
//        Seed seed = result.seed();
//        assertThat(seed.code()).isEqualTo(DefaultAddressFactory.Code.INSTANCE_IS_NOT_STR.getValue());
//        assertThat(seed.args()).isEmpty();
//    }

//    @Test
//    void shouldCheckCreation_ifBlank() {
//        Result<Address> result = new DefaultAddressFactory().create("");
//
//        assertThat(result.ok()).isFalse();
//        assertThat(result.value()).isNull();
//        Seed seed = result.seed();
//        assertThat(seed.code()).isEqualTo(DefaultAddressFactory.Code.INSTANCE_IS_BLANK.getValue());
//        assertThat(seed.args()).isEmpty();
//    }

//    @Test
//    void shouldCheckCreation() {
//        String expectedValue = Faker.str_().random(5, 10);
//        Result<Address> result = new DefaultAddressFactory().create(expectedValue);
//
//        assertThat(result.ok()).isTrue();
//        assertThat(result.value().value()).isEqualTo(expectedValue);
//        assertThat(result.seed()).isNull();
//    }
}