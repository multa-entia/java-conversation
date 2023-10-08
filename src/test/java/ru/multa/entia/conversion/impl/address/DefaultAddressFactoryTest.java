package ru.multa.entia.conversion.impl.address;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultAddressFactoryTest {
    @Test
    void shouldCheckCreation_checkerFail() {
        String expectedCode = Faker.str_().random();
        Function<Object, Seed> checker =  object -> {
            return createSeed(expectedCode);
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
        Function<Object, Seed> checker =  object -> {return null;};
        Function<String, Address> creator = DefaultAddressFactoryTest::createAddress;

        Result<Address> result = new DefaultAddressFactory(checker, creator).create(expectedValue);

        assertThat(result.ok()).isTrue();
        assertThat(result.value().value()).isEqualTo(expectedValue);
        assertThat(result.seed()).isNull();
    }

    private static Seed createSeed(final String code){
        Seed seed = Mockito.mock(Seed.class);
        Mockito
                .when(seed.code())
                .thenReturn(code);

        return seed;
    }

    private static Address createAddress(final String value){
        Address address = Mockito.mock(Address.class);
        Mockito
                .when(address.value())
                .thenReturn(value);

        return address;
    }
}