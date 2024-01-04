package ru.multa.entia.conversion.impl.content;

import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.value.Value;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Seeds;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultContentCheckerTest {

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckChecking_ifInstanceIsNull() {
        Seed seed = new DefaultContentChecker().check(null);

        assertThat(Seeds.comparator(seed).code(CR.get(DefaultContentChecker.Code.IS_NULL)).compare()).isTrue();
    }

    @Test
    void shouldCheckChecking_ifInstanceHasBadParent() {
        Seed seed = new DefaultContentChecker().check(new BadParentTestValue());

        assertThat(Seeds.comparator(seed).code(CR.get(DefaultContentChecker.Code.BAD_PARENT)).compare()).isTrue();
    }

    @Test
    void shouldCheckChecking() {
        Seed seed = new DefaultContentChecker().check(new TestValue(Faker.int_().random()));

        assertThat(Seeds.comparator(seed).isNull().compare()).isTrue();
    }

    private static class BadParentTestValue {}
    public record TestValue(int x) implements Value {}
}