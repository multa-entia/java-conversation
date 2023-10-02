package ru.multa.entia.conversion.impl.confirmation;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultConfirmationFactoryTest {

    @Test
    void shouldCheckDefaultFromGetter_ifArgsNull() {
        DefaultConfirmationFactory.Decorator decorator
                = new DefaultConfirmationFactory.DefaultFromDecoratorGetter().apply(null);

        String expected = Faker.str_().random();
        assertThat(decorator.decorate(expected)).isEqualTo(expected);
    }

    @Test
    void shouldCheckDefaultFromGetter_ifKeyIsAbsence() {
        DefaultConfirmationFactory.Decorator decorator
                = new DefaultConfirmationFactory.DefaultFromDecoratorGetter().apply(new Object[]{});

        String expected = Faker.str_().random();
        assertThat(decorator.decorate(expected)).isEqualTo(expected);
    }

    @Test
    void shouldCheckDefaultFromGetter_ifValueIsAbsence() {
        Object[] args = {null, DefaultConfirmationFactory.KEY__FROM_DECORATOR};
        DefaultConfirmationFactory.Decorator decorator
                = new DefaultConfirmationFactory.DefaultFromDecoratorGetter().apply(args);

        String expected = Faker.str_().random();
        assertThat(decorator.decorate(expected)).isEqualTo(expected);
    }

    @Test
    void shouldCheckDefaultFromGetter() {

        DefaultConfirmationFactory.Decorator d = (s) -> {return s; };
        Object[] args = {null, DefaultConfirmationFactory.KEY__FROM_DECORATOR, d};
        DefaultConfirmationFactory.Decorator decorator
                = new DefaultConfirmationFactory.DefaultFromDecoratorGetter().apply(args);

        String expected = Faker.str_().random();
        assertThat(decorator.decorate(expected)).isEqualTo(expected);
    }
}