package ru.multa.entia.conversion.impl.pipeline;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.pipeline.PipelineSubscriber;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.fakers.impl.Faker;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPipelineBoxHandlerTaskTest {
    @Test
    void shouldCheckBoxGetting() {
        Supplier<TestPipelineBox> testPipelineBoxSupplier = () -> {
            return Mockito.mock(TestPipelineBox.class);
        };
        TestPipelineBox expectedBox = testPipelineBoxSupplier.get();

        PipelineBox<PublisherTask<Message>> box = new DefaultPipelineBoxHandlerTask<Message>(
                expectedBox,
                null,
                null,
                null
        ).box();

        assertThat(box).isEqualTo(expectedBox);
    }

    @Test
    void shouldCheckActorGetting() {
        Supplier<TestPipelineSubscriber> testPipelineSubscriberSupplier = () -> {
            return Mockito.mock(TestPipelineSubscriber.class);
        };

        HashMap<UUID, PipelineSubscriber<PublisherTask<Message>>> expectedActor = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            expectedActor.put(Faker.uuid_().random(), testPipelineSubscriberSupplier.get());
        }

        Map<UUID, PipelineSubscriber<PublisherTask<Message>>> actor = new DefaultPipelineBoxHandlerTask<Message>(
                null,
                expectedActor,
                null,
                null
        ).actor();

        assertThat(actor).isEqualTo(expectedActor);
    }

    @Test
    void shouldCheckSessionIdGetting() {
        UUID expectedSessionId = Faker.uuid_().random();
        UUID sessionId = new DefaultPipelineBoxHandlerTask<Message>(
                null,
                null,
                expectedSessionId,
                null
        ).sessionId();

        assertThat(sessionId).isEqualTo(expectedSessionId);
    }

    @Test
    void shouldCheckActorLockGetting() {
        Lock expectedActorLock = new ReentrantLock();
        Lock actorLock = new DefaultPipelineBoxHandlerTask<Message>(
                null,
                null,
                null,
                expectedActorLock
        ).actorLock();

        assertThat(actorLock).isEqualTo(expectedActorLock);
    }

    private interface TestPipelineBox extends PipelineBox<PublisherTask<Message>> {}
    private interface TestPipelineSubscriber extends PipelineSubscriber<PublisherTask<Message>> {}
}