package ru.multa.entia.conversion.impl.type;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Seeds;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTypeValueCheckerTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckChecking_ifInstanceNull() {
        Seed seed = new DefaultTypeValueChecker().check(null);

        assertThat(Seeds.comparator(seed).code(CR.get(DefaultTypeValueChecker.Code.IS_NULL)).compare()).isTrue();
    }

    @Test
    void shouldCheckChecking_ifNotStr() {
        Seed seed = new DefaultTypeValueChecker().check(Faker.uuid_().random());

        assertThat(Seeds.comparator(seed).code(CR.get(DefaultTypeValueChecker.Code.IS_NOT_STR)).compare()).isTrue();
    }

    @Test
    void shouldCheckSuccessChecking() {
        Seed seed = new DefaultTypeValueChecker().check(Faker.str_().random());

        assertThat(Seeds.comparator(seed).isNull().compare()).isTrue();
    }
}