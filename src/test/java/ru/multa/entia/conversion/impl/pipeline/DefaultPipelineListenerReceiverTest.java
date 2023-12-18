package ru.multa.entia.conversion.impl.pipeline;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.pipeline.PipelineSubscriber;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.utils.Results;
import utils.ResultUtil;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPipelineListenerReceiverTest {

    private static final Function<UUID, TestPipelineSubscriber> TEST_PIPELINE_SUBSCRIBER_FUNCTION = id -> {
        TestPipelineSubscriber subscriber = Mockito.mock(TestPipelineSubscriber.class);
        Mockito.when(subscriber.getId()).thenReturn(id);

        return subscriber;
    };

    private static final Function<TestListenerTask, TestPipelineBox> TEST_PIPELINE_BOX_FUNCTION = task -> {
        TestPipelineBox box = Mockito.mock(TestPipelineBox.class);
        Mockito.when(box.value()).thenReturn(task);

        return box;
    };

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckBlockOut() {
        UUID expectedSessionId = Faker.uuid_().random();
        DefaultPipelineListenerReceiver<Message> receiver = new DefaultPipelineListenerReceiver<>();
        Result<Object> result = receiver.blockOut(expectedSessionId);

        Field field = receiver.getClass().getDeclaredField("sessionId");
        field.setAccessible(true);
        AtomicReference<UUID> gottenSessionId = (AtomicReference<UUID>) field.get(receiver);

        assertThat(Results.comparator(result).isSuccess().value(null).compare()).isTrue();
        assertThat(gottenSessionId.get()).isEqualTo(expectedSessionId);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckBlockOut_ifAlreadyBlockedOut() {
        UUID firstSessionId = Faker.uuid_().random();
        UUID secondSessionId = Faker.uuid_().random();
        DefaultPipelineListenerReceiver<Message> receiver = new DefaultPipelineListenerReceiver<>();
        receiver.blockOut(firstSessionId);
        Result<Object> result = receiver.blockOut(secondSessionId);

        Field field = receiver.getClass().getDeclaredField("sessionId");
        field.setAccessible(true);
        AtomicReference<UUID> gottenSessionId = (AtomicReference<UUID>) field.get(receiver);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(DefaultPipelineListenerReceiver.Code.ALREADY_BLOCKED_OUT.getValue())
                        .back()
                        .compare()
        ).isTrue();
        assertThat(gottenSessionId.get()).isEqualTo(firstSessionId);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckBlock_ifAlreadyBlocked() {
        DefaultPipelineListenerReceiver<Message> receiver = new DefaultPipelineListenerReceiver<>();
        Result<Object> result = receiver.block();

        Field field = receiver.getClass().getDeclaredField("sessionId");
        field.setAccessible(true);
        AtomicReference<UUID> gottenSessionId = (AtomicReference<UUID>) field.get(receiver);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(DefaultPipelineListenerReceiver.Code.ALREADY_BLOCKED.getValue())
                        .back()
                        .compare()
        ).isTrue();
        assertThat(gottenSessionId.get()).isNull();
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckBlock() {
        DefaultPipelineListenerReceiver<Message> receiver = new DefaultPipelineListenerReceiver<>();
        receiver.blockOut(Faker.uuid_().random());
        Result<Object> result = receiver.block();

        Field field = receiver.getClass().getDeclaredField("sessionId");
        field.setAccessible(true);
        AtomicReference<UUID> gottenSessionId = (AtomicReference<UUID>) field.get(receiver);

        assertThat(Results.comparator(result).isSuccess().value(null).compare()).isTrue();
        assertThat(gottenSessionId.get()).isNull();
    }

    @Test
    void shouldCheckSubscription() {
        DefaultPipelineListenerReceiver<Message> receiver = new DefaultPipelineListenerReceiver<>();
        UUID expectedKey = Faker.uuid_().random();
        TestPipelineSubscriber expectedSubscriber = TEST_PIPELINE_SUBSCRIBER_FUNCTION.apply(expectedKey);

        Result<PipelineSubscriber<ListenerTask<Message>>> result = receiver.subscribe(expectedSubscriber);

        assertThat(Results.comparator(result).isSuccess().value(expectedSubscriber).compare()).isTrue();
    }

    @Test
    void shouldCheckSubscription_ifAlreadySubscribed() {
        DefaultPipelineListenerReceiver<Message> receiver = new DefaultPipelineListenerReceiver<>();
        UUID expectedKey = Faker.uuid_().random();
        TestPipelineSubscriber expectedSubscriber = TEST_PIPELINE_SUBSCRIBER_FUNCTION.apply(expectedKey);

        receiver.subscribe(expectedSubscriber);
        Result<PipelineSubscriber<ListenerTask<Message>>> result = receiver.subscribe(expectedSubscriber);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(DefaultPipelineListenerReceiver.Code.ALREADY_SUBSCRIBED.getValue())
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckUnsubscription() {
        DefaultPipelineListenerReceiver<Message> receiver = new DefaultPipelineListenerReceiver<>();
        UUID expectedKey = Faker.uuid_().random();
        TestPipelineSubscriber expectedSubscriber = TEST_PIPELINE_SUBSCRIBER_FUNCTION.apply(expectedKey);

        receiver.subscribe(expectedSubscriber);
        Result<PipelineSubscriber<ListenerTask<Message>>> result = receiver.unsubscribe(expectedSubscriber);

        assertThat(Results.comparator(result).isSuccess().value(expectedSubscriber).compare()).isTrue();
    }

    @Test
    void shouldCheckUnsubscription_ifAlreadyUnsubscribed() {
        DefaultPipelineListenerReceiver<Message> receiver = new DefaultPipelineListenerReceiver<>();
        UUID expectedKey = Faker.uuid_().random();
        TestPipelineSubscriber expectedSubscriber = TEST_PIPELINE_SUBSCRIBER_FUNCTION.apply(expectedKey);

        receiver.subscribe(expectedSubscriber);
        receiver.unsubscribe(expectedSubscriber);
        Result<PipelineSubscriber<ListenerTask<Message>>> result = receiver.unsubscribe(expectedSubscriber);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(DefaultPipelineListenerReceiver.Code.ALREADY_UNSUBSCRIBED.getValue())
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckReceiving_ifBlocked() {
        DefaultPipelineListenerReceiver<Message> receiver = new DefaultPipelineListenerReceiver<>();
        Result<Object> result = receiver.receive(Faker.uuid_().random(), TEST_PIPELINE_BOX_FUNCTION.apply(null));

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(DefaultPipelineListenerReceiver.Code.IS_BLOCKED.getValue())
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckReceiving_ifBadSessionId() {
        UUID rightSessionId = Faker.uuid_().random();
        UUID badSessionId = null;
        for (int i = 0; i < 10; i++) {
            badSessionId = Faker.uuid_().random();
            if (!badSessionId.equals(rightSessionId)){
                break;
            }
        }

        assertThat(rightSessionId).isNotEqualTo(badSessionId);

        DefaultPipelineListenerReceiver<Message> receiver = new DefaultPipelineListenerReceiver<>();
        receiver.blockOut(rightSessionId);
        Result<Object> result = receiver.receive(badSessionId, TEST_PIPELINE_BOX_FUNCTION.apply(null));

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(DefaultPipelineListenerReceiver.Code.INVALID_SESSION_ID.getValue())
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckReceiving_ifSubscriberIsAbsence() {
        UUID sessionId = Faker.uuid_().random();

        DefaultPipelineListenerReceiver<Message> receiver = new DefaultPipelineListenerReceiver<>();
        receiver.blockOut(sessionId);
        Result<Object> result = receiver.receive(sessionId, TEST_PIPELINE_BOX_FUNCTION.apply(null));

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(DefaultPipelineListenerReceiver.Code.NO_ONE_SUBSCRIBER.getValue())
                        .back()
                        .compare()
        ).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckReceiving() {
        AtomicInteger counter = new AtomicInteger(0);
        UUID sessionId = Faker.uuid_().random();

        Function<UUID, TestPipelineSubscriber> uuidTestPipelineSubscriberFunction = key -> {
            TestPipelineSubscriber subscriber = TEST_PIPELINE_SUBSCRIBER_FUNCTION.apply(key);
            Mockito
                    .when(subscriber.give(Mockito.any(), Mockito.any()))
                    .thenAnswer(new Answer<Result<Object>>() {
                        @Override
                        public Result<Object> answer(InvocationOnMock invocation) throws Throwable {
                            counter.incrementAndGet();

                            return ResultUtil.ok(null);
                        }
                    });

            return subscriber;
        };

        DefaultPipelineListenerReceiver<Message> receiver = new DefaultPipelineListenerReceiver<>();
        Integer subscriberAmount = Faker.int_().between(3, 7);
        for (int i = 0; i < subscriberAmount; i++) {
            receiver.subscribe(uuidTestPipelineSubscriberFunction.apply(Faker.uuid_().random()));
        }
        receiver.blockOut(sessionId);

        Integer receivingAmount = Faker.int_().between(10, 20);
        for (int i = 0; i < receivingAmount; i++) {
            Result<Object> result = receiver.receive(sessionId, TEST_PIPELINE_BOX_FUNCTION.apply(null));
            assertThat(Results.comparator(result).isSuccess().value(null).compare()).isTrue();
        }

        Thread.sleep(10);

        assertThat(counter.get()).isEqualTo(subscriberAmount * receivingAmount);
    }

    private interface TestPipelineSubscriber extends PipelineSubscriber<ListenerTask<Message>> {}
    private interface TestListenerTask extends ListenerTask<Message> {}
    private interface TestPipelineBox extends PipelineBox<ListenerTask<Message>> {}
}