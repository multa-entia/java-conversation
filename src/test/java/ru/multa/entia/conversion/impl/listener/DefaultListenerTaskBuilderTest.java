package ru.multa.entia.conversion.impl.listener;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.conversion.api.listener.ListenerService;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.conversion.api.listener.ListenerTaskBuilder;
import ru.multa.entia.conversion.api.listener.ListenerTaskCreator;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.utils.Results;
import utils.FakerUtil;
import utils.ResultUtil;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultListenerTaskBuilderTest {

    private static final ListenerTaskCreator<Message> CREATOR = item -> {
        TestListenerTask task = Mockito.mock(TestListenerTask.class);
        Mockito.when(task.item()).thenReturn(item);

        return task;
    };

    @SneakyThrows
    @Test
    void shouldCheckItemSetting() {
        DefaultListenerTaskBuilder<Message> expectedBuilder = new DefaultListenerTaskBuilder<>();
        Message expectedMessage = FakerUtil.randomMessage();
        ListenerTaskBuilder<Message> builder = expectedBuilder.item(expectedMessage);

        assertThat(builder).isEqualTo(expectedBuilder);

        Field field = builder.getClass().getDeclaredField("item");
        field.setAccessible(true);
        Message message = (Message) field.get(builder);

        assertThat(message).isEqualTo(expectedMessage);
    }

    @Test
    void shouldCheckBuilding() {
        Message expectedItem = FakerUtil.randomMessage();
        ListenerTask<Message> task = new DefaultListenerTaskBuilder<Message>(null, CREATOR)
                .item(expectedItem)
                .build();

        assertThat(task.item()).isEqualTo(expectedItem);
    }

    @Test
    void shouldCheckBuildingAndListening_ifServiceIsNull() {
        Message expectedItem = FakerUtil.randomMessage();
        Result<ListenerTask<Message>> result = new DefaultListenerTaskBuilder<Message>(null, CREATOR)
                .item(expectedItem)
                .buildAndListen();


        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(DefaultListenerTaskBuilder.Code.SERVICE_IS_NULL.getValue())
                        .argsAreEmpty()
                        .back()
                        .compare()

        ).isTrue();
    }

    @Test
    void shouldCheckBuildingAndListening_ifServiceReturnsBadResult() {
        String expectedCode = Faker.str_().random();
        Supplier<TestListenerService> serviceSupplier = () -> {
            TestListenerService service = Mockito.mock(TestListenerService.class);
            Mockito
                    .when(service.listen(Mockito.any(TestListenerTask.class)))
                    .thenReturn(ResultUtil.fail(expectedCode));

            return service;
        };

        Message expectedItem = FakerUtil.randomMessage();
        Result<ListenerTask<Message>> result = new DefaultListenerTaskBuilder<Message>(serviceSupplier.get(), CREATOR)
                .item(expectedItem)
                .buildAndListen();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(expectedCode)
                        .argsAreEmpty()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckBuildingAndPublishing() {
        Message expectedItem = FakerUtil.randomMessage();
        Supplier<TestListenerService> serviceSupplier = () -> {
            TestListenerService service = Mockito.mock(TestListenerService.class);
            Result<ListenerTask<Message>> result = ResultUtil.ok(CREATOR.create(expectedItem));
            Mockito
                    .when(service.listen(Mockito.any(TestListenerTask.class)))
                    .thenReturn(result);

            return service;
        };

        Result<ListenerTask<Message>> result = new DefaultListenerTaskBuilder<Message>(serviceSupplier.get(), CREATOR)
                .item(expectedItem)
                .buildAndListen();

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(expectedItem)
                        .seedsComparator()
                        .isNull()
                        .compare()
        ).isTrue();
    }

    private interface TestListenerTask extends ListenerTask<Message> {}
    private interface TestListenerService extends ListenerService<Message> {}
}