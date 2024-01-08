package ru.multa.entia.conversion.impl.address;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Seeds;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultAddressCheckerTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckDefaultAddressValueChecker_ifInstanceIsNull() {
        Seed seed = new DefaultAddressChecker().check(null);

        boolean result = Seeds.comparator(seed)
                .code(CR.get(DefaultAddressChecker.Code.INSTANCE_IS_NULL))
                .argsAreEmpty()
                .compare();
        assertThat(result).isTrue();
    }

    @Test
    void shouldCheckDefaultAddressValueChecker_ifInstanceIsNotString() {
        Seed seed = new DefaultAddressChecker().check(Faker.int_().random());

        boolean result = Seeds.comparator(seed)
                .code(CR.get(DefaultAddressChecker.Code.INSTANCE_IS_NOT_STR))
                .argsAreEmpty()
                .compare();
        assertThat(result).isTrue();
    }

    @Test
    void shouldCheckDefaultAddressValueChecker_ifInstanceIsEmpty() {
        Seed seed = new DefaultAddressChecker().check("");

        boolean result = Seeds.comparator(seed)
                .code(CR.get(DefaultAddressChecker.Code.INSTANCE_IS_BLANK))
                .argsAreEmpty()
                .compare();
        assertThat(result).isTrue();
    }

    @Test
    void shouldCheckDefaultAddressValueChecker_ifInstanceIsBlank() {
        Seed seed = new DefaultAddressChecker().check("  ");

        boolean result = Seeds.comparator(seed)
                .code(CR.get(DefaultAddressChecker.Code.INSTANCE_IS_BLANK))
                .argsAreEmpty()
                .compare();
        assertThat(result).isTrue();
    }

    @Test
    void shouldCheckDefaultAddressValueChecker() {
        String expectedValue = Faker.str_().random(5, 10);
        Seed seed = new DefaultAddressChecker().check(expectedValue);

        assertThat(Seeds.comparator(seed).isNull().compare()).isTrue();
    }
}