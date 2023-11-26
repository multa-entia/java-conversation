package ru.multa.entia.conversion.impl.message;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.conversion.api.SimpleFactory;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.utils.Results;
import utils.*;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultMessageFactoryTest {
    private static final Function<Seed, TestChecker> CHECKER_FUNC = seed -> {
        TestChecker checker = Mockito.mock(TestChecker.class);
        Mockito.when(checker.check(Mockito.any())).thenReturn(seed);

        return checker;
    };

    private static final Function<Result<Content>, TestContentFactory> CONTENT_FACTORY_FUNC = result -> {
        TestContentFactory factory = Mockito.mock(TestContentFactory.class);
        Mockito.when(factory.create(Mockito.any(), Mockito.any())).thenReturn(result);

        return factory;
    };

    private static final Function<Result<UUID>, TestUuidGetter> UUID_GETTER_FUNC = result -> {
        TestUuidGetter getter = Mockito.mock(TestUuidGetter.class);
        Mockito.when(getter.apply(Mockito.any())).thenReturn(result);

        return getter;
    };

    private static final Function<Result<Address>, TestAddressGetter> ADDRESS_GETTER_FUNC = result -> {
        TestAddressGetter getter = Mockito.mock(TestAddressGetter.class);
        Mockito.when(getter.apply(Mockito.any())).thenReturn(result);

        return getter;
    };

    private static final Function<Result<Boolean>, TestBooleanGetter> BOOLEAN_GETTER_FUNC = result -> {
        TestBooleanGetter getter = Mockito.mock(TestBooleanGetter.class);
        Mockito.when(getter.apply(Mockito.any())).thenReturn(result);

        return getter;
    };

    @Test
    void shouldCheckCreation_ifCheckerRetBadResult() {
        String expectedCode = Faker.str_().random();
        Result<Message> result = new DefaultMessageFactory(
                CHECKER_FUNC.apply(ResultUtil.seed(expectedCode)),
                null,
                null,
                null,
                null,
                null,
                null,
                null
        ).create(null);

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(expectedCode)
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckCreation_ifContentFactoryRetBadResult() {
        String expectedCode = Faker.str_().random();
        Result<Message> result = new DefaultMessageFactory(
                CHECKER_FUNC.apply(null),
                CONTENT_FACTORY_FUNC.apply(DefaultResultBuilder.<Content>fail(expectedCode)),
                null,
                null,
                null,
                null,
                null,
                null
        ).create(null);

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(expectedCode)
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckCreation_ifIdGetterRetBadResult() {
        String expectedCode = Faker.str_().random();
        TestContent content = new TestContent(new TestType(Faker.str_().random()), Faker.str_().random());
        Result<Message> result = new DefaultMessageFactory(
                CHECKER_FUNC.apply(null),
                CONTENT_FACTORY_FUNC.apply(DefaultResultBuilder.<Content>ok(content)),
                UUID_GETTER_FUNC.apply(DefaultResultBuilder.<UUID>fail(expectedCode)),
                null,
                null,
                null,
                null,
                null
        ).create(null);

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(expectedCode)
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckCreation_ifConversationGetterRetBadResult() {
        String expectedCode = Faker.str_().random();
        TestContent content = new TestContent(new TestType(Faker.str_().random()), Faker.str_().random());
        Result<Message> result = new DefaultMessageFactory(
                CHECKER_FUNC.apply(null),
                CONTENT_FACTORY_FUNC.apply(DefaultResultBuilder.<Content>ok(content)),
                UUID_GETTER_FUNC.apply(DefaultResultBuilder.<UUID>ok(Faker.uuid_().random())),
                UUID_GETTER_FUNC.apply(DefaultResultBuilder.<UUID>fail(expectedCode)),
                null,
                null,
                null,
                null
        ).create(null);

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(expectedCode)
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckCreation_ifFromGetterRetBadResult() {
        String expectedCode = Faker.str_().random();
        TestContent content = new TestContent(new TestType(Faker.str_().random()), Faker.str_().random());
        Result<Message> result = new DefaultMessageFactory(
                CHECKER_FUNC.apply(null),
                CONTENT_FACTORY_FUNC.apply(DefaultResultBuilder.<Content>ok(content)),
                UUID_GETTER_FUNC.apply(DefaultResultBuilder.<UUID>ok(Faker.uuid_().random())),
                UUID_GETTER_FUNC.apply(DefaultResultBuilder.<UUID>ok(Faker.uuid_().random())),
                ADDRESS_GETTER_FUNC.apply(DefaultResultBuilder.<Address>fail(expectedCode)),
                null,
                null,
                null
        ).create(null);

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(expectedCode)
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckCreation_ifToGetterRetBadResult() {
        String expectedCode = Faker.str_().random();
        TestContent content = new TestContent(new TestType(Faker.str_().random()), Faker.str_().random());
        Result<Message> result = new DefaultMessageFactory(
                CHECKER_FUNC.apply(null),
                CONTENT_FACTORY_FUNC.apply(DefaultResultBuilder.<Content>ok(content)),
                UUID_GETTER_FUNC.apply(DefaultResultBuilder.<UUID>ok(Faker.uuid_().random())),
                UUID_GETTER_FUNC.apply(DefaultResultBuilder.<UUID>ok(Faker.uuid_().random())),
                ADDRESS_GETTER_FUNC.apply(DefaultResultBuilder.<Address>ok(new TestAddress(Faker.str_().random()))),
                ADDRESS_GETTER_FUNC.apply(DefaultResultBuilder.<Address>fail(expectedCode)),
                null,
                null
        ).create(null);

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(expectedCode)
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckCreation_ifConfirmGetterRetBadResult() {
        String expectedCode = Faker.str_().random();
        TestContent content = new TestContent(new TestType(Faker.str_().random()), Faker.str_().random());
        Result<Message> result = new DefaultMessageFactory(
                CHECKER_FUNC.apply(null),
                CONTENT_FACTORY_FUNC.apply(DefaultResultBuilder.<Content>ok(content)),
                UUID_GETTER_FUNC.apply(DefaultResultBuilder.<UUID>ok(Faker.uuid_().random())),
                UUID_GETTER_FUNC.apply(DefaultResultBuilder.<UUID>ok(Faker.uuid_().random())),
                ADDRESS_GETTER_FUNC.apply(DefaultResultBuilder.<Address>ok(new TestAddress(Faker.str_().random()))),
                ADDRESS_GETTER_FUNC.apply(DefaultResultBuilder.<Address>ok(new TestAddress(Faker.str_().random()))),
                BOOLEAN_GETTER_FUNC.apply(DefaultResultBuilder.<Boolean>fail(expectedCode)),
                null
        ).create(null);

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(expectedCode)
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckCreation() {
        AtomicReference<Object> checkerInstanceHolder = new AtomicReference<>();
        AtomicReference<Object> contentFactoryInstanceHolder = new AtomicReference<>();
        AtomicReference<Object> contentFactoryArgHolder = new AtomicReference<>();
        AtomicReference<Object> idGetterHolder = new AtomicReference<>();
        AtomicReference<Object> conversationGetterHolder = new AtomicReference<>();
        AtomicReference<Object> fromGetterHolder = new AtomicReference<>();
        AtomicReference<Object> toGetterHolder = new AtomicReference<>();
        AtomicReference<Object> confirmGetterHolder = new AtomicReference<>();

        String instance = Faker.str_().random();
        Long arg = Faker.long_().random();
        Object[] args = {arg};

        Supplier<TestChecker> checkerSup = () -> {
            TestChecker checker = Mockito.mock(TestChecker.class);
            Mockito
                    .when(checker.check(Mockito.any()))
                    .thenAnswer(new Answer<Seed>() {
                        @Override
                        public Seed answer(InvocationOnMock invocation) throws Throwable {
                            checkerInstanceHolder.set(invocation.getArguments()[0]);
                            return null;
                        }
                    });

            return checker;
        };

        Function<Result<Content>, TestContentFactory> contentFactoryFunc = result -> {
            TestContentFactory factory = Mockito.mock(TestContentFactory.class);
            Mockito
                    .when(factory.create(Mockito.any(), Mockito.any()))
                    .thenAnswer(new Answer<Result<Content>>() {
                        @Override
                        public Result<Content> answer(InvocationOnMock invocation) throws Throwable {
                            contentFactoryInstanceHolder.set(invocation.getArguments()[0]);
                            contentFactoryArgHolder.set(invocation.getArguments()[1]);
                            return result;
                        }
                    });

            return factory;
        };

        Function<Result<UUID>, TestUuidGetter> idGetterFunc = result -> {
            TestUuidGetter getter = Mockito.mock(TestUuidGetter.class);
            Mockito
                    .when(getter.apply(Mockito.any()))
                    .thenAnswer(new Answer<Result<UUID>>() {
                        @Override
                        public Result<UUID> answer(InvocationOnMock invocation) throws Throwable {
                            idGetterHolder.set(invocation.getArguments()[0]);
                            return result;
                        }
                    });

            return getter;
        };

        Function<Result<UUID>, TestUuidGetter> conversationGetterFunc = result -> {
            TestUuidGetter getter = Mockito.mock(TestUuidGetter.class);
            Mockito
                    .when(getter.apply(Mockito.any()))
                    .thenAnswer(new Answer<Result<UUID>>() {
                        @Override
                        public Result<UUID> answer(InvocationOnMock invocation) throws Throwable {
                            conversationGetterHolder.set(invocation.getArguments()[0]);
                            return result;
                        }
                    });

            return getter;
        };

        Function<Result<Address>, TestAddressGetter> fromGetterFunc = result -> {
            TestAddressGetter getter = Mockito.mock(TestAddressGetter.class);
            Mockito
                    .when(getter.apply(Mockito.any()))
                    .thenAnswer(new Answer<Result<Address>>() {
                        @Override
                        public Result<Address> answer(InvocationOnMock invocation) throws Throwable {
                            fromGetterHolder.set(invocation.getArguments()[0]);
                            return result;
                        }
                    });

            return getter;
        };

        Function<Result<Address>, TestAddressGetter> toGetterFunc = result -> {
            TestAddressGetter getter = Mockito.mock(TestAddressGetter.class);
            Mockito
                    .when(getter.apply(Mockito.any()))
                    .thenAnswer(new Answer<Result<Address>>() {
                        @Override
                        public Result<Address> answer(InvocationOnMock invocation) throws Throwable {
                            toGetterHolder.set(invocation.getArguments()[0]);
                            return result;
                        }
                    });

            return getter;
        };

        Function<Result<Boolean>, TestBooleanGetter> confirmGetterFunc = result -> {
            TestBooleanGetter getter = Mockito.mock(TestBooleanGetter.class);
            Mockito
                    .when(getter.apply(Mockito.any()))
                    .thenAnswer(new Answer<Result<Boolean>>() {
                        @Override
                        public Result<Boolean> answer(InvocationOnMock invocation) throws Throwable {
                            confirmGetterHolder.set(invocation.getArguments()[0]);
                            return result;
                        }
                    });

            return getter;
        };

        TestContent expectedContent = new TestContent(new TestType(Faker.str_().random()), Faker.str_().random());
        UUID expectedId = Faker.uuid_().random();
        UUID expectedConversation = Faker.uuid_().random();
        TestAddress expectedFrom = new TestAddress(Faker.str_().random());
        TestAddress expectedTo = new TestAddress(Faker.str_().random());
        boolean expectedConfirm = true;
        Result<Message> result = new DefaultMessageFactory(
                checkerSup.get(),
                contentFactoryFunc.apply(DefaultResultBuilder.<Content>ok(expectedContent)),
                idGetterFunc.apply(DefaultResultBuilder.<UUID>ok(expectedId)),
                conversationGetterFunc.apply(DefaultResultBuilder.<UUID>ok(expectedConversation)),
                fromGetterFunc.apply(DefaultResultBuilder.<Address>ok(expectedFrom)),
                toGetterFunc.apply(DefaultResultBuilder.<Address>ok(expectedTo)),
                confirmGetterFunc.apply(DefaultResultBuilder.<Boolean>ok(expectedConfirm)),
                new DefaultMessageCreator()
        ).create(instance, arg);

        assertThat(Results.comparator(result)
                .isSuccess()
                .seedsComparator()
                .isNull()
                .back()
                .compare()).isTrue();

        Message message = result.value();
        assertThat(message.id()).isEqualTo(expectedId);
        assertThat(message.conversation()).isEqualTo(expectedConversation);
        assertThat(message.from()).isEqualTo(expectedFrom);
        assertThat(message.to()).isEqualTo(expectedTo);
        assertThat(message.confirm()).isEqualTo(expectedConfirm);
        assertThat(message.content()).isEqualTo(expectedContent);

        assertThat(checkerInstanceHolder.get()).isEqualTo(instance);
        assertThat(contentFactoryInstanceHolder.get()).isEqualTo(instance);
        assertThat(contentFactoryArgHolder.get()).isEqualTo(arg);
        assertThat(idGetterHolder.get()).isEqualTo(args);
        assertThat(conversationGetterHolder.get()).isEqualTo(args);
        assertThat(fromGetterHolder.get()).isEqualTo(args);
        assertThat(toGetterHolder.get()).isEqualTo(args);
        assertThat(confirmGetterHolder.get()).isEqualTo(args);
    }

    private interface TestChecker extends Checker<Object> {}
    private interface TestContentFactory extends SimpleFactory<Object, Content> {}
    private interface TestUuidGetter extends Function<Object[], Result<UUID>> {}
    private interface TestAddressGetter extends Function<Object[], Result<Address>> {}
    private interface TestBooleanGetter extends Function<Object[], Result<Boolean>> {}
}
