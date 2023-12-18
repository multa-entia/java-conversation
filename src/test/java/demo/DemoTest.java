package demo;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.conversion.impl.pipeline.DefaultPipelineBox;
import ru.multa.entia.conversion.impl.publisher.DefaultPublisherService;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import utils.FakerUtil;

import java.util.concurrent.ArrayBlockingQueue;

class DemoTest {

    @SneakyThrows
    @Test
    void run() {
        DefaultPublisherService<Message> service = new DefaultPublisherService<>(DefaultResultBuilder::<PublisherTask<Message>>ok);

        ArrayBlockingQueue<PipelineBox<PublisherTask<Message>>> publisherQueue = new ArrayBlockingQueue<>(100);
        ArrayBlockingQueue<Message> senderQueue = new ArrayBlockingQueue<>(100);
        PublisherDemo publisherDemo = PublisherDemo.create(
                10,
                publisherQueue,
                senderQueue
        );
        publisherDemo.start();

        for (int i = 0; i < 3; i++) {
            publisherQueue.offer(new DefaultPipelineBox<>(service.builder().item(FakerUtil.randomMessage()).build()));
        }

        Thread.sleep(3_000);

        System.out.println("Sender queue size: " + senderQueue.size());
    }
}
