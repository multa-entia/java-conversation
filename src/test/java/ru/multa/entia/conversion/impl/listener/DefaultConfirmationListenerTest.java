package ru.multa.entia.conversion.impl.listener;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.conversion.api.confirmation.Confirmation;
import ru.multa.entia.conversion.api.holder.Holder;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.utils.Results;
import utils.FakerUtil;
import utils.ResultUtil;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;


class DefaultConfirmationListenerTest {

    @Test
    void shouldCheckListening_ifHolderReleaseFail() {
        String expectedCode = Faker.str_().random();
        Supplier<Holder> holderSupplier = () -> {
            Holder holder = Mockito.mock(Holder.class);
            Mockito
                    .when(holder.release(Mockito.any()))
                    .thenReturn(ResultUtil.fail(expectedCode));

            return holder;
        };

        Supplier<TestListenerTask> taskSupplier = () -> {
            TestListenerTask task = Mockito.mock(TestListenerTask.class);
            Mockito.when(task.item()).thenReturn(FakerUtil.randomConfirm());

            return task;
        };

        DefaultConfirmationListener listener = new DefaultConfirmationListener(holderSupplier.get());
        Result<Confirmation> result = listener.listen(taskSupplier.get());

        assertThat(
                Results
                        .comparator(result)
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
        Supplier<Holder> holderSupplier = () -> {
            Holder holder = Mockito.mock(Holder.class);
            Mockito
                    .when(holder.release(Mockito.any()))
                    .thenReturn(ResultUtil.ok(null));

            return holder;
        };

        Confirmation expectedConversation = FakerUtil.randomConfirm();
        Supplier<TestListenerTask> taskSupplier = () -> {
            TestListenerTask task = Mockito.mock(TestListenerTask.class);
            Mockito.when(task.item()).thenReturn(expectedConversation);

            return task;
        };

        DefaultConfirmationListener listener = new DefaultConfirmationListener(holderSupplier.get());
        Result<Confirmation> result = listener.listen(taskSupplier.get());

        assertThat(
                Results
                        .comparator(result)
                        .isSuccess()
                        .value(expectedConversation)
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();
    }

    private interface TestListenerTask extends ListenerTask<Confirmation> {}
}