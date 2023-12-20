package demo;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.conversion.impl.listener.DefaultListenerService;
import ru.multa.entia.conversion.impl.pipeline.DefaultPipelineBox;
import ru.multa.entia.conversion.impl.publisher.DefaultPublisherService;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import utils.FakerUtil;

import java.util.concurrent.ArrayBlockingQueue;

class DemoTest {

    @SneakyThrows
    @Test
    void run() {
        DefaultPublisherService<Message> publisherService = new DefaultPublisherService<>(DefaultResultBuilder::<PublisherTask<Message>>ok);
        DefaultListenerService<Message> listenerService = new DefaultListenerService<>(DefaultResultBuilder::<ListenerTask<Message>>ok);

        ArrayBlockingQueue<PipelineBox<PublisherTask<Message>>> publisherQueue = new ArrayBlockingQueue<>(100);
        ArrayBlockingQueue<Message> senderQueue = new ArrayBlockingQueue<>(100);
        ArrayBlockingQueue<PipelineBox<ListenerTask<Message>>> listenerQueue = new ArrayBlockingQueue<>(100);
        ArrayBlockingQueue<Message> listenerStrategyQueue = new ArrayBlockingQueue<Message>(100);

        PublisherDemo publisherDemo = PublisherDemo.create(
                publisherQueue,
                senderQueue
        );
        publisherDemo.start();

        ListenerDemo listenerDemo = ListenerDemo.create(
                listenerQueue,
                listenerStrategyQueue
        );
        listenerDemo.start();

        Thread midThread = new Thread(() -> {
            while (true) {
                try {
                    Message take = senderQueue.take();
                    listenerQueue.offer(new DefaultPipelineBox<>(listenerService.builder().item(FakerUtil.randomMessage()).build()));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        midThread.start();

        Thread publisherThread = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                publisherQueue.offer(new DefaultPipelineBox<>(publisherService.builder().item(FakerUtil.randomMessage()).build()));
            }
        });
        publisherThread.start();

        Thread.sleep(3_000);

        System.out.println("Sender queue size: " + senderQueue.size());
        System.out.println("Listener strategy queue : " + listenerStrategyQueue.size());
    }
}
