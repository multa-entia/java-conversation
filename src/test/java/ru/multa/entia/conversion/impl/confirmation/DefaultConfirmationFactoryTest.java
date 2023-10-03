package ru.multa.entia.conversion.impl.confirmation;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultConfirmationFactoryTest {

    @Test
    void shouldCheckFromGetter_ifArgsNull() {
        DefaultConfirmationFactory.Decorator decorator
                = new DefaultConfirmationFactory.FromDecoratorGetter().apply(null);

        String expected = Faker.str_().random();
        assertThat(decorator.decorate(expected)).isEqualTo(expected);
    }

    @Test
    void shouldCheckFromGetter_ifKeyIsAbsence() {
        DefaultConfirmationFactory.Decorator decorator
                = new DefaultConfirmationFactory.FromDecoratorGetter().apply(new Object[]{});

        String expected = Faker.str_().random();
        assertThat(decorator.decorate(expected)).isEqualTo(expected);
    }

    @Test
    void shouldCheckFromGetter_ifValueIsAbsence() {
        Object[] args = {null, DefaultConfirmationFactory.KEY__FROM_DECORATOR};
        DefaultConfirmationFactory.Decorator decorator
                = new DefaultConfirmationFactory.FromDecoratorGetter().apply(args);

        String expected = Faker.str_().random();
        assertThat(decorator.decorate(expected)).isEqualTo(expected);
    }

    @Test
    void shouldCheckFromGetter() {
        DefaultConfirmationFactory.Decorator d = (s) -> {return s; };
        Object[] args = {null, DefaultConfirmationFactory.KEY__FROM_DECORATOR, d};
        DefaultConfirmationFactory.Decorator decorator
                = new DefaultConfirmationFactory.FromDecoratorGetter().apply(args);

        String expected = Faker.str_().random();
        assertThat(decorator.decorate(expected)).isEqualTo(expected);
    }

    @Test
    void shouldCheckCodeGetter_ifArgsNull() {
        String code = new DefaultConfirmationFactory.CodeGetter().apply(null);

        assertThat(code).isNull();
    }

    @Test
    void shouldCheckCodeGetter_ifKeyIsAbsence() {
        String code = new DefaultConfirmationFactory.CodeGetter().apply(new Object[0]);

        assertThat(code).isNull();
    }

    @Test
    void shouldCheckCodeGetter_ifValueIsAbsence() {
        Object[] args = {null, DefaultConfirmationFactory.KEY__CODE};
        String code = new DefaultConfirmationFactory.CodeGetter().apply(args);

        assertThat(code).isNull();
    }

    @Test
    void shouldCheckCodeGetter() {
        String expectedCode = Faker.str_().random(5, 10);
        Object[] args = {null, DefaultConfirmationFactory.KEY__CODE, expectedCode};
        String code = new DefaultConfirmationFactory.CodeGetter().apply(args);

        assertThat(code).isEqualTo(expectedCode);
    }

    @Test
    void shouldCheckArgsGetter_ifArgsNull() {
        Object[] args = new DefaultConfirmationFactory.ArgsGetter().apply(null);

        assertThat(args).isEmpty();
    }

    @Test
    void shouldCheckArgsGetter_ifKeyIsAbsence() {
        Object[] args = new DefaultConfirmationFactory.ArgsGetter().apply(new Object[0]);

        assertThat(args).isEmpty();
    }

    @Test
    void shouldCheckArgsGetter_ifValueIsAbsence() {
        Object[] inputArgs = {null, DefaultConfirmationFactory.KEY__ARGS};
        Object[] args = new DefaultConfirmationFactory.ArgsGetter().apply(inputArgs);

        assertThat(args).isEmpty();
    }

    @Test
    void shouldCheckArgsGetter() {
        String arg0 = Faker.str_().random();
        String arg1 = Faker.str_().random();
        String arg2 = Faker.str_().random();
        Object[] expectedArgs = {arg0, arg1, arg2};

        Object[] inputArgs = {null, DefaultConfirmationFactory.KEY__ARGS, arg0, arg1, arg2};
        Object[] args = new DefaultConfirmationFactory.ArgsGetter().apply(inputArgs);

        assertThat(args).isEqualTo(expectedArgs);
    }
}