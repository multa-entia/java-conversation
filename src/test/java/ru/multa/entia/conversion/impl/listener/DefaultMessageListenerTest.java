package ru.multa.entia.conversion.impl.listener;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.conversion.api.listener.ListenerStrategy;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.utils.Results;
import utils.FakerUtil;
import utils.ResultUtil;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultMessageListenerTest {

    private static final Function<Message, ListenerTask<Message>> TASK_FUNCTION = message -> {
        TestListenerTask task = Mockito.mock(TestListenerTask.class);
        Mockito.when(task.item()).thenReturn(message);

        return task;
    };

    @Test
    void shouldCheckListening_ifFail() {
        String expectedCode = Faker.str_().random();
        Supplier<TestListenerStrategy> strategySupplier = () -> {
            TestListenerStrategy strategy = Mockito.mock(TestListenerStrategy.class);
            Mockito
                    .when(strategy.execute(Mockito.any()))
                    .thenReturn(ResultUtil.fail(expectedCode));

            return strategy;
        };

        Result<Message> result = new DefaultMessageListener(strategySupplier.get())
                .listen(TASK_FUNCTION.apply(FakerUtil.randomMessage()));

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(expectedCode)
                        .argsAreEmpty()
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckListening() {
        Message expectedMessage = FakerUtil.randomMessage();
        Supplier<TestListenerStrategy> strategySupplier = () -> {
            TestListenerStrategy strategy = Mockito.mock(TestListenerStrategy.class);
            Mockito
                    .when(strategy.execute(Mockito.any()))
                    .thenReturn(ResultUtil.ok(expectedMessage));

            return strategy;
        };

        Result<Message> result = new DefaultMessageListener(strategySupplier.get())
                .listen(TASK_FUNCTION.apply(null));

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(expectedMessage)
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();
    }

    private interface TestListenerStrategy extends ListenerStrategy<Message> {}
    private interface TestListenerTask extends ListenerTask<Message> {}
}