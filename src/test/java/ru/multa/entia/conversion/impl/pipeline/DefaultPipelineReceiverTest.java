package ru.multa.entia.conversion.impl.pipeline;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.pipeline.PipelineSubscriber;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import utils.ResultUtil;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: 18.11.2023 faked bool
class DefaultPipelineReceiverTest {

    private static final Function<UUID, TestPipelineSubscriber> TEST_PIPELINE_SUBSCRIBER_FUNCTION = id -> {
        TestPipelineSubscriber subscriber = Mockito.mock(TestPipelineSubscriber.class);
        Mockito.when(subscriber.getId()).thenReturn(id);

        return subscriber;
    };

    private static final Function<TestPublisherTask, TestPipelineBox> TEST_PIPELINE_BOX_FUNCTION = task -> {
        TestPipelineBox box = Mockito.mock(TestPipelineBox.class);
        Mockito.when(box.value()).thenReturn(task);

        return box;
    };

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckBlockOut() {
        UUID expectedSessionId = Faker.uuid_().random();
        DefaultPipelineReceiver<Message> receiver = new DefaultPipelineReceiver<>();
        Result<Object> result = receiver.blockOut(expectedSessionId);

        Field field = receiver.getClass().getDeclaredField("sessionId");
        field.setAccessible(true);
        AtomicReference<UUID> gottenSessionId = (AtomicReference<UUID>) field.get(receiver);

        assertThat(ResultUtil.isEqual(result, ResultUtil.ok(null))).isTrue();
        assertThat(gottenSessionId.get()).isEqualTo(expectedSessionId);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckBlockOut_ifAlreadyBlockedOut() {
        UUID firstSessionId = Faker.uuid_().random();
        UUID secondSessionId = Faker.uuid_().random();
        DefaultPipelineReceiver<Message> receiver = new DefaultPipelineReceiver<>();
        receiver.blockOut(firstSessionId);
        Result<Object> result = receiver.blockOut(secondSessionId);

        Field field = receiver.getClass().getDeclaredField("sessionId");
        field.setAccessible(true);
        AtomicReference<UUID> gottenSessionId = (AtomicReference<UUID>) field.get(receiver);

        assertThat(ResultUtil.isEqual(
                result,
                ResultUtil.fail(DefaultPipelineReceiver.Code.ALREADY_BLOCKED_OUT.getValue()))).isTrue();
        assertThat(gottenSessionId.get()).isEqualTo(firstSessionId);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckBlock_ifAlreadyBlocked() {
        DefaultPipelineReceiver<Message> receiver = new DefaultPipelineReceiver<>();
        Result<Object> result = receiver.block();

        Field field = receiver.getClass().getDeclaredField("sessionId");
        field.setAccessible(true);
        AtomicReference<UUID> gottenSessionId = (AtomicReference<UUID>) field.get(receiver);

        assertThat(ResultUtil.isEqual(
                result,
                ResultUtil.fail(DefaultPipelineReceiver.Code.ALREADY_BLOCKED.getValue()))).isTrue();
        assertThat(gottenSessionId.get()).isNull();
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckBlock() {
        DefaultPipelineReceiver<Message> receiver = new DefaultPipelineReceiver<>();
        receiver.blockOut(Faker.uuid_().random());
        Result<Object> result = receiver.block();

        Field field = receiver.getClass().getDeclaredField("sessionId");
        field.setAccessible(true);
        AtomicReference<UUID> gottenSessionId = (AtomicReference<UUID>) field.get(receiver);

        assertThat(ResultUtil.isEqual(result, ResultUtil.ok(null))).isTrue();
        assertThat(gottenSessionId.get()).isNull();
    }

    @Test
    void shouldCheckSubscription() {
        DefaultPipelineReceiver<Message> receiver = new DefaultPipelineReceiver<>();
        UUID expectedKey = Faker.uuid_().random();
        TestPipelineSubscriber expectedSubscriber = TEST_PIPELINE_SUBSCRIBER_FUNCTION.apply(expectedKey);

        Result<PipelineSubscriber<PublisherTask<Message>>> result = receiver.subscribe(expectedSubscriber);

        assertThat(ResultUtil.isEqual(result, ResultUtil.ok(expectedSubscriber))).isTrue();
    }

    @Test
    void shouldCheckSubscription_ifAlreadySubscribed() {
        DefaultPipelineReceiver<Message> receiver = new DefaultPipelineReceiver<>();
        UUID expectedKey = Faker.uuid_().random();
        TestPipelineSubscriber expectedSubscriber = TEST_PIPELINE_SUBSCRIBER_FUNCTION.apply(expectedKey);

        receiver.subscribe(expectedSubscriber);
        Result<PipelineSubscriber<PublisherTask<Message>>> result = receiver.subscribe(expectedSubscriber);

        assertThat(ResultUtil.isEqual(
                result,
                ResultUtil.fail(DefaultPipelineReceiver.Code.ALREADY_SUBSCRIBED.getValue()))).isTrue();
    }

    @Test
    void shouldCheckUnsubscription() {
        DefaultPipelineReceiver<Message> receiver = new DefaultPipelineReceiver<>();
        UUID expectedKey = Faker.uuid_().random();
        TestPipelineSubscriber expectedSubscriber = TEST_PIPELINE_SUBSCRIBER_FUNCTION.apply(expectedKey);

        receiver.subscribe(expectedSubscriber);
        Result<PipelineSubscriber<PublisherTask<Message>>> result = receiver.unsubscribe(expectedSubscriber);

        assertThat(ResultUtil.isEqual(result, ResultUtil.ok(expectedSubscriber))).isTrue();
    }

    @Test
    void shouldCheckUnsubscription_ifAlreadyUnsubscribed() {
        DefaultPipelineReceiver<Message> receiver = new DefaultPipelineReceiver<>();
        UUID expectedKey = Faker.uuid_().random();
        TestPipelineSubscriber expectedSubscriber = TEST_PIPELINE_SUBSCRIBER_FUNCTION.apply(expectedKey);

        receiver.subscribe(expectedSubscriber);
        receiver.unsubscribe(expectedSubscriber);
        Result<PipelineSubscriber<PublisherTask<Message>>> result = receiver.unsubscribe(expectedSubscriber);

        assertThat(ResultUtil.isEqual(
                result,
                ResultUtil.fail(DefaultPipelineReceiver.Code.ALREADY_UNSUBSCRIBED.getValue()))).isTrue();
    }

    @Test
    void shouldCheckReceiving_ifBlocked() {
        DefaultPipelineReceiver<Message> receiver = new DefaultPipelineReceiver<>();
        Result<Object> result = receiver.receive(Faker.uuid_().random(), TEST_PIPELINE_BOX_FUNCTION.apply(null));

        assertThat(ResultUtil.isEqual(
                result,
                ResultUtil.fail(DefaultPipelineReceiver.Code.IS_BLOCKED.getValue()))).isTrue();
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

        DefaultPipelineReceiver<Message> receiver = new DefaultPipelineReceiver<>();
        receiver.blockOut(rightSessionId);
        Result<Object> result = receiver.receive(badSessionId, TEST_PIPELINE_BOX_FUNCTION.apply(null));

        assertThat(ResultUtil.isEqual(
                result,
                ResultUtil.fail(DefaultPipelineReceiver.Code.INVALID_SESSION_ID.getValue()))).isTrue();
    }

    @Test
    void shouldCheckReceiving_ifSubscriberIsAbsence() {
        UUID sessionId = Faker.uuid_().random();

        DefaultPipelineReceiver<Message> receiver = new DefaultPipelineReceiver<>();
        receiver.blockOut(sessionId);
        Result<Object> result = receiver.receive(sessionId, TEST_PIPELINE_BOX_FUNCTION.apply(null));

        assertThat(ResultUtil.isEqual(
                result,
                ResultUtil.fail(DefaultPipelineReceiver.Code.NO_ONE_SUBSCRIBER.getValue()))).isTrue();
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

                            return DefaultResultBuilder.<Object>ok(null);
                        }
                    });

            return subscriber;
        };

        DefaultPipelineReceiver<Message> receiver = new DefaultPipelineReceiver<>();
        Integer subscriberAmount = Faker.int_().between(3, 7);
        for (int i = 0; i < subscriberAmount; i++) {
            receiver.subscribe(uuidTestPipelineSubscriberFunction.apply(Faker.uuid_().random()));
        }
        receiver.blockOut(sessionId);

        Integer receivingAmount = Faker.int_().between(10, 20);
        for (int i = 0; i < receivingAmount; i++) {
            Result<Object> result = receiver.receive(sessionId, TEST_PIPELINE_BOX_FUNCTION.apply(null));
            assertThat(ResultUtil.isEqual(result, ResultUtil.ok(null))).isTrue();
        }

        Thread.sleep(10);

        assertThat(counter.get()).isEqualTo(subscriberAmount * receivingAmount);
    }

    private interface TestPipelineSubscriber extends PipelineSubscriber<PublisherTask<Message>> {}
    private interface TestPublisherTask extends PublisherTask<Message> {}
    private interface TestPipelineBox extends PipelineBox<PublisherTask<Message>> {}
}