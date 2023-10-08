package ru.multa.entia.conversion.impl.confirmation;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.confirmation.Confirmation;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultConfirmationFactoryTest {

    // TODO: 08.10.2023 restore
//    @Test
//    void shouldCheckFromGetter_ifArgsNull() {
//        DefaultConfirmationFactory.Decorator decorator
//                = new DefaultConfirmationFactory.FromDecoratorGetter().apply(null);
//
//        String expected = Faker.str_().random();
//        assertThat(decorator.decorate(createTestAddress(expected)).value()).isEqualTo(expected);
//    }
//
//    @Test
//    void shouldCheckFromGetter_ifKeyIsAbsence() {
//        DefaultConfirmationFactory.Decorator decorator
//                = new DefaultConfirmationFactory.FromDecoratorGetter().apply(new Object[]{});
//
//        String expected = Faker.str_().random();
//        assertThat(decorator.decorate(createTestAddress(expected)).value()).isEqualTo(expected);
//    }
//
//    @Test
//    void shouldCheckFromGetter_ifValueIsAbsence() {
//        Object[] args = {null, DefaultConfirmationFactory.KEY__FROM_DECORATOR};
//        DefaultConfirmationFactory.Decorator decorator
//                = new DefaultConfirmationFactory.FromDecoratorGetter().apply(args);
//
//        String expected = Faker.str_().random();
//        assertThat(decorator.decorate(createTestAddress(expected)).value()).isEqualTo(expected);
//    }
//
//    @Test
//    void shouldCheckFromGetter() {
//        DefaultConfirmationFactory.Decorator d = (s) -> {return s; };
//        Object[] args = {null, DefaultConfirmationFactory.KEY__FROM_DECORATOR, d};
//        DefaultConfirmationFactory.Decorator decorator
//                = new DefaultConfirmationFactory.FromDecoratorGetter().apply(args);
//
//        String expected = Faker.str_().random();
//        assertThat(decorator.decorate(createTestAddress(expected)).value()).isEqualTo(expected);
//    }
//
//    @Test
//    void shouldCheckCodeGetter_ifArgsNull() {
//        String code = new DefaultConfirmationFactory.CodeGetter().apply(null);
//
//        assertThat(code).isNull();
//    }
//
//    @Test
//    void shouldCheckCodeGetter_ifKeyIsAbsence() {
//        String code = new DefaultConfirmationFactory.CodeGetter().apply(new Object[0]);
//
//        assertThat(code).isNull();
//    }
//
//    @Test
//    void shouldCheckCodeGetter_ifValueIsAbsence() {
//        Object[] args = {null, DefaultConfirmationFactory.KEY__CODE};
//        String code = new DefaultConfirmationFactory.CodeGetter().apply(args);
//
//        assertThat(code).isNull();
//    }
//
//    @Test
//    void shouldCheckCodeGetter() {
//        String expectedCode = Faker.str_().random(5, 10);
//        Object[] args = {null, DefaultConfirmationFactory.KEY__CODE, expectedCode};
//        String code = new DefaultConfirmationFactory.CodeGetter().apply(args);
//
//        assertThat(code).isEqualTo(expectedCode);
//    }
//
//    @Test
//    void shouldCheckArgsGetter_ifArgsNull() {
//        Object[] args = new DefaultConfirmationFactory.ArgsGetter().apply(null);
//
//        assertThat(args).isEmpty();
//    }
//
//    @Test
//    void shouldCheckArgsGetter_ifKeyIsAbsence() {
//        Object[] args = new DefaultConfirmationFactory.ArgsGetter().apply(new Object[0]);
//
//        assertThat(args).isEmpty();
//    }
//
//    @Test
//    void shouldCheckArgsGetter_ifValueIsAbsence() {
//        Object[] inputArgs = {null, DefaultConfirmationFactory.KEY__ARGS};
//        Object[] args = new DefaultConfirmationFactory.ArgsGetter().apply(inputArgs);
//
//        assertThat(args).isEmpty();
//    }
//
//    @Test
//    void shouldCheckArgsGetter() {
//        String arg0 = Faker.str_().random();
//        String arg1 = Faker.str_().random();
//        String arg2 = Faker.str_().random();
//        Object[] expectedArgs = {arg0, arg1, arg2};
//
//        Object[] inputArgs = {null, DefaultConfirmationFactory.KEY__ARGS, arg0, arg1, arg2};
//        Object[] args = new DefaultConfirmationFactory.ArgsGetter().apply(inputArgs);
//
//        assertThat(args).isEqualTo(expectedArgs);
//    }
//
//    @Test
//    void shouldCheckCreation_ifMessageNull() {
//        Result<Confirmation> result = new DefaultConfirmationFactory().create(null);
//
//        assertThat(result.ok()).isFalse();
//        assertThat(result.value()).isNull();
//        assertThat(result.seed().code()).isEqualTo(DefaultConfirmationFactory.CODE__MESSAGE_NULL);
//    }
//
//    @Test
//    void shouldCheckCreation_ifIdMessageNull() {
//        Result<Confirmation> result = new DefaultConfirmationFactory().create(createTestMessage(
//                null,
//                Faker.uuid_().random(),
//                createTestAddress(Faker.str_().random()),
//                createTestAddress(Faker.str_().random())
//        ));
//
//        assertThat(result.ok()).isFalse();
//        assertThat(result.value()).isNull();
//        assertThat(result.seed().code()).isEqualTo(DefaultConfirmationFactory.CODE__FIELD_NULL);
//        Object[] args = result.seed().args();
//        assertThat(args).hasSize(1);
//        assertThat(args[0]).isEqualTo(DefaultConfirmationFactory.ALIAS__ID);
//    }
//
//    @Test
//    void shouldCheckCreation_ifConversationMessageNull() {
//        Result<Confirmation> result = new DefaultConfirmationFactory().create(createTestMessage(
//                Faker.uuid_().random(),
//                null,
//                createTestAddress(Faker.str_().random()),
//                createTestAddress(Faker.str_().random())
//        ));
//
//        assertThat(result.ok()).isFalse();
//        assertThat(result.value()).isNull();
//        assertThat(result.seed().code()).isEqualTo(DefaultConfirmationFactory.CODE__FIELD_NULL);
//        Object[] args = result.seed().args();
//        assertThat(args).hasSize(1);
//        assertThat(args[0]).isEqualTo(DefaultConfirmationFactory.ALIAS__CONVERSATION);
//    }
//
//    @Test
//    void shouldCheckCreation_ifFromMessageNull() {
//        Result<Confirmation> result = new DefaultConfirmationFactory().create(createTestMessage(
//                Faker.uuid_().random(),
//                Faker.uuid_().random(),
//                null,
//                createTestAddress(Faker.str_().random())
//        ));
//
//        assertThat(result.ok()).isFalse();
//        assertThat(result.value()).isNull();
//        assertThat(result.seed().code()).isEqualTo(DefaultConfirmationFactory.CODE__FIELD_NULL);
//        Object[] args = result.seed().args();
//        assertThat(args).hasSize(1);
//        assertThat(args[0]).isEqualTo(DefaultConfirmationFactory.ALIAS__FROM);
//    }
//
//    @Test
//    void shouldCheckCreation_ifToMessageNull() {
//        Result<Confirmation> result = new DefaultConfirmationFactory().create(createTestMessage(
//                Faker.uuid_().random(),
//                Faker.uuid_().random(),
//                createTestAddress(Faker.str_().random()),
//                null
//        ));
//
//        assertThat(result.ok()).isFalse();
//        assertThat(result.value()).isNull();
//        assertThat(result.seed().code()).isEqualTo(DefaultConfirmationFactory.CODE__FIELD_NULL);
//        Object[] args = result.seed().args();
//        assertThat(args).hasSize(1);
//        assertThat(args[0]).isEqualTo(DefaultConfirmationFactory.ALIAS__TO);
//    }
//
//    @Test
//    void shouldCheckCreation_ifAllMessageFieldNull() {
//        Result<Confirmation> result = new DefaultConfirmationFactory().create(createTestMessage(
//                null,
//                null,
//                null,
//                null
//        ));
//
//        String expectedArg = DefaultConfirmationFactory.ALIAS__ID +
//                DefaultConfirmationFactory.ALIAS__CONVERSATION +
//                DefaultConfirmationFactory.ALIAS__FROM +
//                DefaultConfirmationFactory.ALIAS__TO;
//        assertThat(result.ok()).isFalse();
//        assertThat(result.value()).isNull();
//        assertThat(result.seed().code()).isEqualTo(DefaultConfirmationFactory.CODE__FIELD_NULL);
//        Object[] args = result.seed().args();
//        assertThat(args).hasSize(1);
//        assertThat(args[0]).isEqualTo(expectedArg);
//    }
//
//    @Test
//    void shouldCheckCreation_withDecorator() {
//        DefaultConfirmationFactory.Decorator d = (s) -> {
//            return createTestAddress(String.format("__%s__", s.value()));
//        };
//        Object[] args = {null, DefaultConfirmationFactory.KEY__FROM_DECORATOR, d};
//
//        UUID initId = Faker.uuid_().random();
//        UUID initConversation = Faker.uuid_().random();
//        Address initFrom = createTestAddress(Faker.str_().random());
//        Address initTo = createTestAddress(Faker.str_().random());
//        Result<Confirmation> result = new DefaultConfirmationFactory().create(
//                createTestMessage(initId, initConversation, initFrom, initTo),
//                args
//        );
//
//        assertThat(result.ok()).isTrue();
//        assertThat(result.seed()).isNull();
//
//        Confirmation confirmation = result.value();
//        assertThat(confirmation.id()).isEqualTo(initId);
//        assertThat(confirmation.conversation()).isEqualTo(initConversation);
//        assertThat(confirmation.from().value()).isEqualTo(d.decorate(initTo).value());
//        assertThat(confirmation.to().value()).isEqualTo(initFrom.value());
//    }
//
//    @Test
//    void shouldCheckCreation_withCodeAndArgs() {
//        String expectedCode = Faker.str_().random();
//        String arg0 = Faker.str_().random();
//        String arg1 = Faker.str_().random();
//        Object[] args = {
//                null,
//                DefaultConfirmationFactory.KEY__CODE,
//                expectedCode,
//                DefaultConfirmationFactory.KEY__ARGS,
//                arg0,
//                arg1
//        };
//
//        Object[] expectedArgs = {arg0, arg1};
//
//        UUID initId = Faker.uuid_().random();
//        UUID initConversation = Faker.uuid_().random();
//        Address initFrom = createTestAddress(Faker.str_().random());
//        Address initTo = createTestAddress(Faker.str_().random());
//        Result<Confirmation> result = new DefaultConfirmationFactory().create(
//                createTestMessage(initId, initConversation, initFrom, initTo),
//                args
//        );
//
//        assertThat(result.ok()).isTrue();
//        assertThat(result.seed()).isNull();
//
//        Confirmation confirmation = result.value();
//        assertThat(confirmation.id()).isEqualTo(initId);
//        assertThat(confirmation.conversation()).isEqualTo(initConversation);
//        assertThat(confirmation.from().value()).isEqualTo(initTo.value());
//        assertThat(confirmation.to().value()).isEqualTo(initFrom.value());
//        assertThat(confirmation.code()).isEqualTo(expectedCode);
//        assertThat(Arrays.equals(confirmation.args(), expectedArgs)).isTrue();
//    }
//
//    @Test
//    void shouldCheckCreation() {
//        UUID initId = Faker.uuid_().random();
//        UUID initConversation = Faker.uuid_().random();
//        Address initFrom = createTestAddress(Faker.str_().random());
//        Address initTo = createTestAddress(Faker.str_().random());
//        Result<Confirmation> result = new DefaultConfirmationFactory().create(
//                createTestMessage(initId, initConversation, initFrom, initTo)
//        );
//
//        assertThat(result.ok()).isTrue();
//        assertThat(result.seed()).isNull();
//
//        Confirmation confirmation = result.value();
//        assertThat(confirmation.id()).isEqualTo(initId);
//        assertThat(confirmation.conversation()).isEqualTo(initConversation);
//        assertThat(confirmation.from().value()).isEqualTo(initTo.value());
//        assertThat(confirmation.to().value()).isEqualTo(initFrom.value());
//        assertThat(confirmation.code()).isNull();
//        assertThat(confirmation.args()).isEmpty();
//    }
//
//    private Address createTestAddress(String value){
//        Address address = Mockito.mock(Address.class);
//        Mockito
//                .when(address.value())
//                .thenReturn(value);
//
//        return address;
//    }
//
//    private Message createTestMessage(UUID id, UUID conversation, Address from, Address to){
//        Message message = Mockito.mock(Message.class);
//        Mockito
//                .when(message.id())
//                .thenReturn(id);
//        Mockito
//                .when(message.conversation())
//                .thenReturn(conversation);
//        Mockito
//                .when(message.from())
//                .thenReturn(from);
//        Mockito
//                .when(message.to())
//                .thenReturn(to);
//
//        return message;
//    }
}