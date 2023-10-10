package ru.multa.entia.conversion.impl.confirmation;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.address.AddressDecorator;
import ru.multa.entia.conversion.api.confirmation.Confirmation;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.impl.address.DefaultAddressDecorator;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultConfirmationFactoryTest {
    @Test
    void shouldCheckCreation_ifFailMessage() {
        String expectedCode = Faker.str_().random();
        DefaultConfirmationFactory factory = new DefaultConfirmationFactory(
                createFailChecker(createSeed(expectedCode)),
                null,
                null,
                null,
                null
        );

        Result<Confirmation> result = factory.create(null);

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(expectedCode);
        assertThat(result.seed().args()).isEmpty();
    }

    @Test
    void shouldCheckCreation_whenDecoratorBadGetting() {
        String expectedCode = Faker.str_().random();
        DefaultConfirmationFactory factory = new DefaultConfirmationFactory(
                createSuccessChecker(),
                null,
                createFailDecoratorGetter(createSeed(expectedCode)),
                null,
                null
        );

        Result<Confirmation> result = factory.create(null);

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(expectedCode);
        assertThat(result.seed().args()).isEmpty();
    }

    @Test
    void shouldCheckCreation_whenCodeBadGetting() {
        String expectedCode = Faker.str_().random();
        DefaultConfirmationFactory factory = new DefaultConfirmationFactory(
                createSuccessChecker(),
                null,
                null,
                createFailCodeGetter(createSeed(expectedCode)),
                null
        );

        Result<Confirmation> result = factory.create(null);

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(expectedCode);
        assertThat(result.seed().args()).isEmpty();
    }

    @Test
    void shouldCheckCreation_whenArgsBadGetting() {
        String expectedCode = Faker.str_().random();
        DefaultConfirmationFactory factory = new DefaultConfirmationFactory(
                createSuccessChecker(),
                null,
                null,
                null,
                createFailArgsGetter(createSeed(expectedCode))
        );

        Result<Confirmation> result = factory.create(null);

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(expectedCode);
        assertThat(result.seed().args()).isEmpty();
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
                this::createConfirmation,
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
        Message message = createTestMessage(
                expectedId,
                expectedConversation,
                createTestAddress(initFromValue),
                createTestAddress(initToValue)
        );

        Result<Confirmation> result = factory.create(message, expectedArgs);

        assertThat(result.ok()).isTrue();
        assertThat(result.seed()).isNull();

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

    private interface TestChecker extends Function<Message, Seed>{}
    private interface TestAddressDecoratorGetter extends Function<Object[], Result<AddressDecorator>>{}
    private interface TestCodeGetter extends Function<Object[], Result<String>> {}
    private interface TestArgsGetter extends Function<Object[], Result<Object[]>> {}

    private TestChecker createSuccessChecker(){
        TestChecker checker = Mockito.mock(TestChecker.class);
        Mockito
                .when(checker.apply(Mockito.any()))
                .thenReturn(null);

        return checker;
    }

    private TestChecker createFailChecker(final Seed seed){
        TestChecker checker = Mockito.mock(TestChecker.class);
        Mockito
                .when(checker.apply(Mockito.any()))
                .thenReturn(seed);

        return checker;
    }

    private TestAddressDecoratorGetter createFailDecoratorGetter(final Seed seed){
        TestAddressDecoratorGetter getter = Mockito.mock(TestAddressDecoratorGetter.class);
        Result<AddressDecorator> result = DefaultResultBuilder.<AddressDecorator>fail(seed);
        Mockito
                .when(getter.apply(Mockito.any()))
                .thenReturn(result);

        return getter;
    }

    private TestCodeGetter createFailCodeGetter(final Seed seed){
        TestCodeGetter getter = Mockito.mock(TestCodeGetter.class);
        Result<String> result = DefaultResultBuilder.<String>fail(seed);
        Mockito
                .when(getter.apply(Mockito.any()))
                .thenReturn(result);

        return getter;
    }

    private TestArgsGetter createFailArgsGetter(final Seed seed){
        TestArgsGetter getter = Mockito.mock(TestArgsGetter.class);
        Result<Object[]> result = DefaultResultBuilder.<Object[]>fail(seed);
        Mockito
                .when(getter.apply(Mockito.any()))
                .thenReturn(result);

        return getter;
    }

    private Seed createSeed(final String code){
        Seed seed = Mockito.mock(Seed.class);
        Mockito
                .when(seed.code())
                .thenReturn(code);

        return seed;
    }

    private Confirmation createConfirmation(final UUID id,
                                            final UUID conversation,
                                            final Address from,
                                            final Address to,
                                            final String code,
                                            final Object[] args){
        Confirmation confirmation = Mockito.mock(Confirmation.class);
        Mockito.when(confirmation.id()).thenReturn(id);
        Mockito.when(confirmation.conversation()).thenReturn(conversation);
        Mockito.when(confirmation.from()).thenReturn(from);
        Mockito.when(confirmation.to()).thenReturn(to);
        Mockito.when(confirmation.code()).thenReturn(code);
        Mockito.when(confirmation.args()).thenReturn(args);

        return confirmation;
    }

    private Address createTestAddress(final String value){
        Address address = Mockito.mock(Address.class);
        Mockito
                .when(address.value())
                .thenReturn(value);

        return address;
    }

    private Message createTestMessage(final UUID id,
                                      final UUID conversation,
                                      final Address from,
                                      final Address to){
        Message message = Mockito.mock(Message.class);
        Mockito
                .when(message.id())
                .thenReturn(id);
        Mockito
                .when(message.conversation())
                .thenReturn(conversation);
        Mockito
                .when(message.from())
                .thenReturn(from);
        Mockito
                .when(message.to())
                .thenReturn(to);

        return message;
    }
}