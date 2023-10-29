package ru.multa.entia.conversion.impl.publisher;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.multa.entia.conversion.api.holder.Holder;
import ru.multa.entia.conversion.api.holder.HolderItem;
import ru.multa.entia.conversion.api.holder.HolderReleaseStrategy;
import ru.multa.entia.conversion.api.holder.HolderTimeoutStrategy;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.conversion.api.sender.Sender;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import utils.FakerUtil;
import utils.TestHolderReleaseStrategy;
import utils.TestHolderTimeoutStrategy;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: 29.10.2023 ME-16
class DefaultMessagePublisherTest {
    @Test
    void shouldCheckPublishing_ifFailSending() {
        String expectedCode = Faker.str_().random();
        Supplier<TestMessageSender> senderSupplier = () -> {
            TestMessageSender sender = Mockito.mock(TestMessageSender.class);
            Mockito.when(sender.send(Mockito.any())).thenReturn(DefaultResultBuilder.<Message>fail(expectedCode));

            return sender;
        };
        Function<Message, TestMessagePublisherTask> taskFunction = message -> {
            TestMessagePublisherTask task = Mockito.mock(TestMessagePublisherTask.class);
            Mockito.when(task.item()).thenReturn(message);

            return task;
        };

        Result<Message> result = new DefaultMessagePublisher(senderSupplier.get())
                .publish(taskFunction.apply(FakerUtil.randomMessage()));

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(expectedCode);
        assertThat(result.seed().args()).isEmpty();
    }

    @Test
    void shouldCheckPublishing_ifFailHolding() {
        String expectedCode = Faker.str_().random();
        Message expectedMessage = FakerUtil.randomMessage();
        Supplier<TestMessageSender> senderSupplier = () -> {
            TestMessageSender sender = Mockito.mock(TestMessageSender.class);
            Mockito.when(sender.send(Mockito.any())).thenReturn(DefaultResultBuilder.<Message>ok(expectedMessage));

            return sender;
        };
        Supplier<Holder> holderSupplier = () -> {
            Holder holder = Mockito.mock(Holder.class);
            Mockito
                    .when(holder.hold(Mockito.any(), Mockito.any(), Mockito.any()))
                    .thenReturn(DefaultResultBuilder.<HolderItem>fail(expectedCode));

            return holder;
        };
        Function<Message, TestMessagePublisherTask> taskFunction = message -> {
            TestMessagePublisherTask task = Mockito.mock(TestMessagePublisherTask.class);
            Mockito.when(task.item()).thenReturn(message);

            return task;
        };

        Result<Message> result = new DefaultMessagePublisher(senderSupplier.get(), holderSupplier.get())
                .publish(taskFunction.apply(expectedMessage));

        assertThat(result.ok()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.seed().code()).isEqualTo(expectedCode);
        assertThat(result.seed().args()).isEmpty();
    }

    @Test
    void shouldCheckPublishing() {
        AtomicReference<Object> senderArgAR = new AtomicReference<>();
        AtomicReference<Object> holderItemArgAR = new AtomicReference<>();
        AtomicReference<Object> holderTimeoutStrategyArgAR = new AtomicReference<>();
        AtomicReference<Object> holderReleaseStrategyArgAR = new AtomicReference<>();

        Message expectedMessage = FakerUtil.randomMessage();
        TestHolderTimeoutStrategy expectedTimeoutStrategy = new TestHolderTimeoutStrategy();
        TestHolderReleaseStrategy expectedReleaseStrategy = new TestHolderReleaseStrategy();

        Supplier<TestMessageSender> senderSupplier = () -> {
            TestMessageSender sender = Mockito.mock(TestMessageSender.class);
            Mockito
                    .when(sender.send(Mockito.any()))
                    .thenAnswer(new Answer<Result<Message>>() {
                        @Override
                        public Result<Message> answer(InvocationOnMock invocation) throws Throwable {
                            senderArgAR.set(invocation.getArguments()[0]);
                            return DefaultResultBuilder.<Message>ok(expectedMessage);
                        }
                    });

            return sender;
        };

        Supplier<Holder> holderSupplier = () -> {
            Holder holder = Mockito.mock(Holder.class);
            Mockito
                    .when(holder.hold(Mockito.any(), Mockito.any(), Mockito.any()))
                    .thenAnswer(new Answer<Result<HolderItem>>() {
                        @Override
                        public Result<HolderItem> answer(InvocationOnMock invocation) throws Throwable {
                            holderItemArgAR.set(invocation.getArgument(0));
                            holderTimeoutStrategyArgAR.set(invocation.getArgument(1));
                            holderReleaseStrategyArgAR.set(invocation.getArgument(2));
                            TestHolderItem item = new TestHolderItem(
                                    invocation.getArgument(0),
                                    invocation.getArgument(1),
                                    invocation.getArgument(2)
                            );
                            return DefaultResultBuilder.<HolderItem>ok(item);
                        }
                    });

            return holder;
        };

        Supplier<TestMessagePublisherTask> taskSupplier = () -> {
            TestMessagePublisherTask task = Mockito.mock(TestMessagePublisherTask.class);
            Mockito.when(task.item()).thenReturn(expectedMessage);
            Mockito.when(task.timeoutStrategy()).thenReturn(expectedTimeoutStrategy);
            Mockito.when(task.releaseStrategy()).thenReturn(expectedReleaseStrategy);

            return task;
        };

        Result<Message> result = new DefaultMessagePublisher(senderSupplier.get(), holderSupplier.get())
                .publish(taskSupplier.get());

        assertThat(result.ok()).isTrue();
        assertThat(result.value()).isEqualTo(expectedMessage);
        assertThat(result.seed()).isNull();

        assertThat(senderArgAR.get()).isEqualTo(expectedMessage);
        assertThat(holderItemArgAR.get()).isEqualTo(expectedMessage);
        assertThat(holderTimeoutStrategyArgAR.get()).isEqualTo(expectedTimeoutStrategy);
        assertThat(holderReleaseStrategyArgAR.get()).isEqualTo(expectedReleaseStrategy);
    }

    private interface TestMessageSender extends Sender<Message> {}
    private interface TestMessagePublisherTask extends PublisherTask<Message> {}
    private record TestHolderItem(Message message, HolderTimeoutStrategy timeoutStrategy, HolderReleaseStrategy releaseStrategy)
            implements HolderItem {}
}