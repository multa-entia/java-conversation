package ru.multa.entia.conversion.impl.content;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.conversion.api.SimpleFactory;
import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.type.Type;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import utils.TestSeed;
import utils.TestType;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: 29.10.2023 ME-16
class DefaultContentFactoryTest {

    private static final Function<Result<Type>, TestTypeFactory> TYPE_FACTORY_FUNC = result -> {
        TestTypeFactory factory = Mockito.mock(TestTypeFactory.class);
        Mockito.when(factory.create(Mockito.any(), Mockito.any())).thenReturn(result);

        return factory;
    };

    private static final Function<Seed, TestContentChecker> CHECKER_FUNC = seed -> {
        TestContentChecker checker = Mockito.mock(TestContentChecker.class);
        Mockito.when(checker.check(Mockito.any())).thenReturn(seed);

        return checker;
    };

    private static final Function<Result<String>, TestSerializer> SERIALIZER_FUNC = result -> {
        TestSerializer serializer = Mockito.mock(TestSerializer.class);
        Mockito.when(serializer.apply(Mockito.any())).thenReturn(result);

        return serializer;
    };

    @Test
    void shouldCheckCreation_typeFactoryRetBadResult() {
        String expectedCode = Faker.str_().random();
        Result<Type> expectedResult = DefaultResultBuilder.<Type>fail(new TestSeed(expectedCode, new Object[0]));

        Result<Content> result = new DefaultContentFactory(
                TYPE_FACTORY_FUNC.apply(expectedResult),
                null,
                null,
                null
        ).create(null);

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(expectedCode);
        assertThat(result.seed().args()).isEmpty();
    }

    @Test
    void shouldCheckCreation_checkerRetBadResult() {
        String expectedCode = Faker.str_().random();

        Result<Content> result = new DefaultContentFactory(
                TYPE_FACTORY_FUNC.apply(DefaultResultBuilder.<Type>ok(new TestType(Faker.str_().random()))),
                CHECKER_FUNC.apply(new TestSeed(expectedCode, new Object[0])),
                null,
                null
        ).create(null);

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(expectedCode);
        assertThat(result.seed().args()).isEmpty();
    }

    @Test
    void shouldCheckCreation_serializerRetBadResult() {
        String expectedCode = Faker.str_().random();

        Result<Content> result = new DefaultContentFactory(
                TYPE_FACTORY_FUNC.apply(DefaultResultBuilder.<Type>ok(new TestType(Faker.str_().random()))),
                CHECKER_FUNC.apply(null),
                SERIALIZER_FUNC.apply(DefaultResultBuilder.<String>fail(new TestSeed(expectedCode, new Object[0]))),
                null
        ).create("12345");

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(expectedCode);
        assertThat(result.seed().args()).isEmpty();
    }

    @Test
    void shouldCheckCreation() {
        String instance = Faker.str_().random(5, 10);
        UUID arg = Faker.uuid_().random();
        Object[] args = {arg};
        TestType expectedType = new TestType(Faker.str_().random());

        AtomicReference<Object> typeFactoryInstanceHolder = new AtomicReference<>();
        AtomicReference<Object> typeFactoryArgHolder = new AtomicReference<>();
        Function<Result<Type>, TestTypeFactory> typeFactoryFunction = result -> {
            TestTypeFactory factory = Mockito.mock(TestTypeFactory.class);
            Mockito
                    .when(factory.create(Mockito.any(), Mockito.any()))
                    .thenAnswer(new Answer<Result<Type>>() {
                        @Override
                        public Result<Type> answer(InvocationOnMock invocation) throws Throwable {
                            typeFactoryInstanceHolder.set(invocation.getArguments()[0]);
                            typeFactoryArgHolder.set(invocation.getArguments()[1]);
                            return result;
                        }
                    });

            return factory;
        };

        AtomicReference<Object> checkerInstanceHolder = new AtomicReference<>();
        Function<Seed, TestContentChecker> checkerFunction = seed -> {
            TestContentChecker checker = Mockito.mock(TestContentChecker.class);
            Mockito
                    .when(checker.check(Mockito.any()))
                    .thenAnswer(new Answer<Seed>() {
                        @Override
                        public Seed answer(InvocationOnMock invocation) throws Throwable {
                            checkerInstanceHolder.set(invocation.getArguments()[0]);
                            return seed;
                        }
                    });

            return checker;
        };

        AtomicReference<Object> serializerInstanceHolder = new AtomicReference<>();
        Function<Result<String>, TestSerializer> serializerFunc = result -> {
            TestSerializer serializer = Mockito.mock(TestSerializer.class);
            Mockito
                    .when(serializer.apply(Mockito.any()))
                    .thenAnswer(new Answer<Result<String>>() {
                        @Override
                        public Result<String> answer(InvocationOnMock invocation) throws Throwable {
                            serializerInstanceHolder.set(invocation.getArguments()[0]);
                            return result;
                        }
                    });

            return serializer;
        };

        String expectedValue = Faker.str_().random();
        Result<Content> result = new DefaultContentFactory(
                typeFactoryFunction.apply(DefaultResultBuilder.<Type>ok(expectedType)),
                checkerFunction.apply(null),
                serializerFunc.apply(DefaultResultBuilder.<String>ok(expectedValue)),
                null
        ).create(instance, args);

        assertThat(result.ok()).isTrue();
        assertThat(result.value().type()).isEqualTo(expectedType);
        assertThat(result.value().value()).isEqualTo(expectedValue);
        assertThat(result.seed()).isNull();
        assertThat(typeFactoryInstanceHolder.get()).isEqualTo(instance);
        assertThat(typeFactoryArgHolder.get()).isEqualTo(arg);
        assertThat(checkerInstanceHolder.get()).isEqualTo(instance);
        assertThat(serializerInstanceHolder.get()).isEqualTo(instance);
    }

    private interface TestTypeFactory extends SimpleFactory<Object, Type> {}
    private interface TestContentChecker extends Checker<Object> {}
    private interface TestSerializer extends Function<Object, Result<String>> {}
}