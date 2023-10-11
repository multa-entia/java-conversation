package ru.multa.entia.conversion.impl.message;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.message.MessageOld;
import ru.multa.entia.conversion.api.value.Value;
import ru.multa.entia.conversion.impl.content.DefaultContentFactory;
import ru.multa.entia.results.api.result.Result;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: 01.10.2023 rename
class DefaultMessageFactoryOldTest {

//    @Test
//    void shouldCheckIdGetting_ifArgsNull() {
//        UUID result = new DefaultMessageFactoryOld.IdGetter().apply(null);
//
//        assertThat(result).isNotNull();
//    }
//
//    @Test
//    void shouldCheckIdGetting_ifArgsDoesNotContainKey() {
//        UUID result = new DefaultMessageFactoryOld.IdGetter().apply(new Object[0]);
//
//        assertThat(result).isNotNull();
//    }
//
//    @Test
//    void shouldCheckIdGetting_ifArgsDoesNotContainValue() {
//        Object[] args = new Object[]{DefaultMessageFactoryOld.KEY__ID};
//        UUID result = new DefaultMessageFactoryOld.IdGetter().apply(args);
//
//        assertThat(result).isNotNull();
//    }
//
//    @Test
//    void shouldCheckIdGetting() {
//        UUID expectedId = UUID.randomUUID();
//        Object[] args = new Object[]{DefaultMessageFactoryOld.KEY__ID, expectedId};
//        UUID result = new DefaultMessageFactoryOld.IdGetter().apply(args);
//
//        assertThat(result).isEqualTo(expectedId);
//    }
//
//    @Test
//    void shouldCheckIsRequestGetting_ifArgsNull() {
//        Boolean result = new DefaultMessageFactoryOld.IsRequestGetter().apply(null);
//
//        assertThat(result).isEqualTo(DefaultMessageFactoryOld.DEFAULT_IS_REQUEST);
//    }
//
//    @Test
//    void shouldCheckIsRequestGetting_ifArgsDoesNotContainKey() {
//        Boolean result = new DefaultMessageFactoryOld.IsRequestGetter().apply(new Object[0]);
//
//        assertThat(result).isEqualTo(DefaultMessageFactoryOld.DEFAULT_IS_REQUEST);
//    }
//
//    @Test
//    void shouldCheckIsRequestGetting_ifArgsDoesNotContainValue() {
//        Object[] args = {DefaultMessageFactoryOld.KEY__IS_REQUEST};
//        Boolean result = new DefaultMessageFactoryOld.IsRequestGetter().apply(args);
//
//        assertThat(result).isEqualTo(DefaultMessageFactoryOld.DEFAULT_IS_REQUEST);
//    }
//
//    @ParameterizedTest
//    @ValueSource(booleans = {false, true})
//    void shouldCheckIsRequestGetting(boolean expected) {
//        Object[] args = new Object[]{DefaultMessageFactoryOld.KEY__IS_REQUEST, expected};
//        Boolean result = new DefaultMessageFactoryOld.IsRequestGetter().apply(args);
//
//        assertThat(result).isEqualTo(expected);
//    }
//
//    @Test
//    void shouldCheckCreation_ifInstanceIsNull() {
//        Result<MessageOld> result = new DefaultMessageFactoryOld().create(null);
//
//        assertThat(result.ok()).isFalse();
//        assertThat(result.value()).isNull();
//        assertThat(result.seed().code()).isEqualTo(DefaultTypeFactory.CODE);
//        assertThat(result.seed().args()).isEmpty();
//    }
//
//    @Test
//    void shouldCheckCreation_ifInstanceIsNotChildOfValue() {
//        Result<MessageOld> result = new DefaultMessageFactoryOld().create(new BadParentTestValue());
//
//        assertThat(result.ok()).isFalse();
//        assertThat(result.value()).isNull();
//        assertThat(result.seed().code()).isEqualTo(DefaultContentFactory.CODE__BAD_PARENT);
//        assertThat(result.seed().args()).isEmpty();
//    }
//
//    @Test
//    void shouldCheckCreation_ifSerializationError() {
//        Result<MessageOld> result = new DefaultMessageFactoryOld().create(new BadAccessTestValue());
//
//        assertThat(result.ok()).isFalse();
//        assertThat(result.value()).isNull();
//        assertThat(result.seed().code()).isEqualTo(DefaultContentFactory.CODE__BAD_SERIALIZATION);
//        assertThat(result.seed().args()).isEmpty();
//    }
//
//    @ParameterizedTest
//    @CsvSource({
//            "false,7ff2709e-3b21-42e7-aca0-e4c5def6d015,123",
//            "true,56641ecc-7d2d-48ab-8d18-73e44ca6820e,456"
//    })
//    void shouldCheckCreation(boolean isRequest, String uuidStr, int testValue) {
//        UUID expectedId = UUID.fromString(uuidStr);
//        TestValue expectedValue = new TestValue(testValue);
//        String expectedContentValue = String.format("{\"x\":%s}", testValue);
//
//        Result<MessageOld> result = new DefaultMessageFactoryOld().create(
//                expectedValue,
//                DefaultMessageFactoryOld.KEY__ID, expectedId,
//                DefaultMessageFactoryOld.KEY__IS_REQUEST, isRequest
//        );
//
//        assertThat(result.ok()).isTrue();
//
//        MessageOld message = result.value();
//        assertThat(message.id()).isEqualTo(expectedId);
//        assertThat(message.isRequest()).isEqualTo(isRequest);
//
//        Content content = message.content();
//        assertThat(content.type().value()).isEqualTo(TestValue.class.getName());
//        assertThat(content.value()).isEqualTo(expectedContentValue);
//
//        assertThat(result.seed()).isNull();
//    }
//
//    private static class BadParentTestValue {}
//
//    private static class BadAccessTestValue implements Value {}
//
//    public record TestValue(int x) implements Value {}
}