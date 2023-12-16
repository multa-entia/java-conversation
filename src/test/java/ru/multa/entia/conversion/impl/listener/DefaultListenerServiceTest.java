package ru.multa.entia.conversion.impl.listener;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.conversion.api.listener.ListenerService;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.conversion.api.listener.ListenerTaskBuilder;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.utils.Results;
import utils.FakerUtil;
import utils.ResultUtil;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DefaultListenerServiceTest {

    @SuppressWarnings("unchecked")
    @Test
    void shouldCheckListening_item() {
        Message expectedMessage = FakerUtil.randomMessage();
        AtomicReference<Object> outputAR = new AtomicReference<>();
        Function<ListenerTask<Message>, Result<ListenerTask<Message>>> output = task -> {
            outputAR.set(task);
            return ResultUtil.ok(task);
        };

        DefaultListenerService<Message> service = new DefaultListenerService<>(output, null, null);
        Result<ListenerTask<Message>> result = service.listen(expectedMessage);

        assertThat(Results.comparator(result).isSuccess().seedsComparator().isNull().back().compare()).isTrue();
        assertThat(result.value()).isNotNull();

        ListenerTask<Message> task = result.value();
        assertThat(task.item()).isEqualTo(expectedMessage);

        ListenerTask<Message> gottenTask = (ListenerTask<Message>) outputAR.get();
        assertThat(gottenTask.item()).isEqualTo(expectedMessage);
    }

    @Test
    void shouldCheckListening_task() {
        Message expectedMessage = FakerUtil.randomMessage();

        AtomicReference<Object> outputAR = new AtomicReference<>();
        Function<ListenerTask<Message>, Result<ListenerTask<Message>>> output = task -> {
            outputAR.set(task);
            return ResultUtil.ok(task);
        };

        DefaultListenerService<Message> service = new DefaultListenerService<>(output, null, null);

        Supplier<TestListenerTask> taskSupplier = () -> {
            TestListenerTask task = Mockito.mock(TestListenerTask.class);
            Mockito.when(task.item()).thenReturn(expectedMessage);

            return task;
        };

        Result<ListenerTask<Message>> result = service.listen(taskSupplier.get());

        assertThat(Results.comparator(result).isSuccess().seedsComparator().isNull().back().compare()).isTrue();
        assertThat(result.value()).isNotNull();

        ListenerTask<Message> task = result.value();
        assertThat(task.item()).isEqualTo(expectedMessage);

        TestListenerTask gottenTask = (TestListenerTask) outputAR.get();
        assertThat(gottenTask.item()).isEqualTo(expectedMessage);
    }

    @Test
    void shouldCheckBuilderGetting() {
        Message expectedMessage = FakerUtil.randomMessage();

        Supplier<TestListenerTask> taskSupplier = () -> {
            TestListenerTask task = Mockito.mock(TestListenerTask.class);
            Mockito.when(task.item()).thenReturn(expectedMessage);

            return task;
        };

        TestListenerTask listenerTask = taskSupplier.get();
        Function<ListenerService<Message>, ListenerTaskBuilder<Message>> builderCreator = service -> {
            TestListenerTaskBuilder builder = Mockito.mock(TestListenerTaskBuilder.class);
            Mockito.when(builder.build()).thenReturn(listenerTask);

            return builder;
        };

        ListenerTaskBuilder<Message> builder = new DefaultListenerService<Message>(null, builderCreator, null)
                .builder();

        assertThat(builder).isNotNull();

        ListenerTask<Message> task = builder.build();
        assertThat(task.item()).isEqualTo(expectedMessage);
    }

    private interface TestListenerTask extends ListenerTask<Message> {}
    private interface TestListenerTaskBuilder extends ListenerTaskBuilder<Message> {}
}