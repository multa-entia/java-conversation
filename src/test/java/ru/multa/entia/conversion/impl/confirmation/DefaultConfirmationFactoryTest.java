package ru.multa.entia.conversion.impl.confirmation;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.address.AddressDecorator;
import ru.multa.entia.conversion.api.confirmation.Confirmation;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.impl.address.DefaultAddressDecorator;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.utils.Results;
import utils.ResultUtil;
import utils.TestAddress;
import utils.TestConfirmation;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultConfirmationFactoryTest {

    @Test
    void shouldCheckCreation_ifFailMessage() {
        String expectedCode = Faker.str_().random();
        TestChecker checker = object -> {
            return ResultUtil.seed(expectedCode);
        };

        DefaultConfirmationFactory factory = new DefaultConfirmationFactory(
                checker,
                null,
                null,
                null,
                null
        );

        Result<Confirmation> result = factory.create(null);

        assertThat(Results
                .comparator(result)
                .isFail()
                .seedsComparator()
                .code(expectedCode)
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckCreation_whenDecoratorBadGetting() {
        String expectedCode = Faker.str_().random();
        TestAddressDecoratorGetter getter = value -> {
            return DefaultResultBuilder.<AddressDecorator>fail(ResultUtil.seed(expectedCode));
        };

        DefaultConfirmationFactory factory = new DefaultConfirmationFactory(
                createSuccessChecker(),
                null,
                getter,
                null,
                null
        );

        Result<Confirmation> result = factory.create(null);

        assertThat(Results
                .comparator(result)
                .isFail()
                .seedsComparator()
                .code(expectedCode)
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckCreation_whenCodeBadGetting() {
        String expectedCode = Faker.str_().random();
        TestCodeGetter getter = array -> {
            return DefaultResultBuilder.<String>fail(ResultUtil.seed(expectedCode));
        };

        DefaultConfirmationFactory factory = new DefaultConfirmationFactory(
                createSuccessChecker(),
                null,
                null,
                getter,
                null
        );

        Result<Confirmation> result = factory.create(null);

        assertThat(Results
                .comparator(result)
                .isFail()
                .seedsComparator()
                .code(expectedCode)
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckCreation_whenArgsBadGetting() {
        String expectedCode = Faker.str_().random();
        TestArgsGetter getter = array -> {
            return DefaultResultBuilder.<Object[]>fail(ResultUtil.seed(expectedCode));
        };

        DefaultConfirmationFactory factory = new DefaultConfirmationFactory(
                createSuccessChecker(),
                null,
                null,
                null,
                getter
        );

        Result<Confirmation> result = factory.create(null);

        assertThat(Results
                .comparator(result)
                .isFail()
                .seedsComparator()
                .code(expectedCode)
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckCreation() {
        String decoratorTemplate = "prefix %s";
        AtomicReference<Object[]> testDecoratorGetterArgs = new AtomicReference<>();
        AtomicReference<Object[]> testCodeGetterArgs = new AtomicReference<>();
        AtomicReference<Object[]> testArgsGetterArgs = new AtomicReference<>();

        Function<Object[], Result<AddressDecorator>> fromDecoratorGetter = args -> {
            testDecoratorGetterArgs.set(args);
            return DefaultResultBuilder.<AddressDecorator>ok(new DefaultAddressDecorator(decoratorTemplate));
        };

        String retCode = Faker.str_().random(5, 10);
        Function<Object[], Result<String>> codeGetter = args -> {
            testCodeGetterArgs.set(args);
            return DefaultResultBuilder.<String>ok(retCode);
        };

        Object[] retArgs = {
                Faker.str_().random(),
                Faker.int_().random()
        };
        Function<Object[], Result<Object[]>> argsGetter = args -> {
            testArgsGetterArgs.set(args);
            return DefaultResultBuilder.<Object[]>ok(retArgs);
        };

        DefaultConfirmationFactory factory = new DefaultConfirmationFactory(
                createSuccessChecker(),
                TestConfirmation::new,
                fromDecoratorGetter,
                codeGetter,
                argsGetter
        );

        Object[] expectedArgs = {
                Faker.uuid_().random(),
                Faker.str_().random(),
                Faker.long_().random()
        };

        UUID expectedId = Faker.uuid_().random();
        UUID expectedConversation = Faker.uuid_().random();
        String initFromValue = Faker.str_().random();
        String initToValue = Faker.str_().random();

        Supplier<Message> messageSupplier = () -> {
            Message message = Mockito.mock(Message.class);
            Mockito.when(message.id()).thenReturn(expectedId);
            Mockito.when(message.conversation()).thenReturn(expectedConversation);
            Address testAddress = new TestAddress(initFromValue);
            Mockito.when(message.from()).thenReturn(testAddress);
            Address testAddress1 = new TestAddress(initToValue);
            Mockito.when(message.to()).thenReturn(testAddress1);

            return message;
        };
        Message message = messageSupplier.get();

        Result<Confirmation> result = factory.create(message, expectedArgs);

        assertThat(Results.comparator(result).isSuccess().seedsComparator().isNull().back().compare()).isTrue();

        Confirmation confirmation = result.value();
        assertThat(confirmation.id()).isEqualTo(expectedId);
        assertThat(confirmation.conversation()).isEqualTo(expectedConversation);
        assertThat(confirmation.from().value()).isEqualTo(String.format(decoratorTemplate, initToValue));
        assertThat(confirmation.to().value()).isEqualTo(initFromValue);
        assertThat(confirmation.code()).isEqualTo(retCode);
        assertThat(confirmation.args()).isEqualTo(retArgs);

        assertThat(testDecoratorGetterArgs.get()).isEqualTo(expectedArgs);
        assertThat(testCodeGetterArgs.get()).isEqualTo(expectedArgs);
        assertThat(testArgsGetterArgs.get()).isEqualTo(expectedArgs);
    }

    private interface TestChecker extends Checker<Message> {}
    private interface TestAddressDecoratorGetter extends Function<Object[], Result<AddressDecorator>>{}
    private interface TestCodeGetter extends Function<Object[], Result<String>> {}
    private interface TestArgsGetter extends Function<Object[], Result<Object[]>> {}

    private TestChecker createSuccessChecker(){
        TestChecker checker = Mockito.mock(TestChecker.class);
        Mockito.when(checker.check(Mockito.any())).thenReturn(null);

        return checker;
    }
}