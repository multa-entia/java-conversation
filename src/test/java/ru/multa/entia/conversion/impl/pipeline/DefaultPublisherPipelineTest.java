package ru.multa.entia.conversion.impl.pipeline;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.results.api.result.Result;
import utils.ResultUtil;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPublisherPipelineTest {

    @SneakyThrows
    @Test
    void shouldCheckStart() {
        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>();
        Result<Object> result = pipeline.start();

        Field field = pipeline.getClass().getDeclaredField("alive");
        field.setAccessible(true);
        AtomicBoolean gottenAlive = (AtomicBoolean) field.get(pipeline);

        assertThat(ResultUtil.isEqual(result, ResultUtil.ok(null))).isTrue();
        assertThat(gottenAlive.get()).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckStart_ifAlreadyStarted() {
        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>();
        pipeline.start();
        Result<Object> result = pipeline.start();

        Field field = pipeline.getClass().getDeclaredField("alive");
        field.setAccessible(true);
        AtomicBoolean gottenAlive = (AtomicBoolean) field.get(pipeline);

        assertThat(ResultUtil.isEqual(result, ResultUtil.fail(DefaultPublisherPipeline.Code.ALREADY_STARTED.getValue())))
                .isTrue();
        assertThat(gottenAlive.get()).isTrue();
    }

    @Test
    void shouldCheckStop() {

    }

    @Test
    void shouldCheckStop_ifAlreadyStopped() {

    }

    @Test
    void shouldCheckSubscription_itIsNotStarted() {

    }

    @Test
    void shouldCheckSubscription_ifAlreadySubscribe() {

    }

    @Test
    void shouldCheckSubscription() {

    }

    @Test
    void shouldCheckUnsubscription_itIsNotStarted() {

    }

    @Test
    void shouldCheckUnsubscription_ifAlreadySubscribe() {

    }

    @Test
    void shouldCheckUnsubscription() {

    }
}