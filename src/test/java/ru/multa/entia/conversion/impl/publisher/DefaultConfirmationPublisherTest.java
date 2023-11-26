package ru.multa.entia.conversion.impl.publisher;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.multa.entia.conversion.api.confirmation.Confirmation;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.conversion.api.sender.Sender;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.utils.Results;
import utils.FakerUtil;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultConfirmationPublisherTest {

    private static final Function<Confirmation, TestConfirmationPublisherTask> TASK_CREATOR = confirmation -> {
        TestConfirmationPublisherTask task = Mockito.mock(TestConfirmationPublisherTask.class);
        Mockito.when(task.item()).thenReturn(confirmation);

        return task;
    };

    @Test
    void shouldCheckPublishing_ifFailSending() {
        String expectedCode = Faker.str_().random();
        Supplier<TestConfirmationSender> supp = () -> {
            TestConfirmationSender sender = Mockito.mock(TestConfirmationSender.class);
            Mockito.when(sender.send(Mockito.any())).thenReturn(DefaultResultBuilder.<Confirmation>fail(expectedCode));

            return sender;
        };

        Result<Confirmation> result = new DefaultConfirmationPublisher(supp.get())
                .publish(TASK_CREATOR.apply(FakerUtil.randomConfirm()));

        assertThat(Results.comparator(result)
                .isFail()
                .seedsComparator()
                .code(expectedCode)
                .back()
                .compare()).isTrue();
    }

    @Test
    void shouldCheckPublishing() {
        Confirmation expectedConfirmation = FakerUtil.randomConfirm();
        AtomicReference<Object> holder = new AtomicReference<>();
        Supplier<TestConfirmationSender> supp = () -> {
            TestConfirmationSender sender = Mockito.mock(TestConfirmationSender.class);
            Mockito
                    .when(sender.send(Mockito.any()))
                    .thenAnswer(new Answer<Result<Confirmation>>() {
                        @Override
                        public Result<Confirmation> answer(InvocationOnMock invocation) throws Throwable {
                            holder.set(invocation.getArguments()[0]);
                            return DefaultResultBuilder.<Confirmation>ok(expectedConfirmation);
                        }
                    });

            return sender;
        };

        Result<Confirmation> result = new DefaultConfirmationPublisher(supp.get())
                .publish(TASK_CREATOR.apply(expectedConfirmation));

        assertThat(Results.comparator(result)
                .isSuccess()
                .value(expectedConfirmation)
                .compare()).isTrue();
        assertThat(holder.get()).isEqualTo(expectedConfirmation);
    }

    private interface TestConfirmationSender extends Sender<Confirmation> {}

    private interface TestConfirmationPublisherTask extends PublisherTask<Confirmation> {}
}