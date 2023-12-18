package demo;

import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.conversion.api.sender.Sender;
import ru.multa.entia.conversion.impl.pipeline.DefaultPipelinePublisherReceiver;
import ru.multa.entia.conversion.impl.pipeline.DefaultPublisherPipeline;
import ru.multa.entia.conversion.impl.pipeline.DefaultPublisherPipelineSubscriber;
import ru.multa.entia.conversion.impl.publisher.DefaultMessagePublisher;
import ru.multa.entia.conversion.impl.publisher.DefaultPublisherService;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.concurrent.BlockingQueue;

public class PublisherDemo extends Thread{

    private final DefaultPublisherPipeline<Message> pipeline;

    public static PublisherDemo create(final int messageQuantity,
                                       final BlockingQueue<PipelineBox<PublisherTask<Message>>> publisherQueue,
                                       final BlockingQueue<Message> senderQueue) {
        DefaultPipelinePublisherReceiver<Message> receiver = new DefaultPipelinePublisherReceiver<>();
        DefaultPublisherPipeline<Message> pipeline = new DefaultPublisherPipeline<>(publisherQueue, receiver);
        pipeline.start();

        DefaultPublisherService<Message> service = new DefaultPublisherService<>(DefaultResultBuilder::<PublisherTask<Message>>ok);

        DefaultMessagePublisher publisher = new DefaultMessagePublisher(new DemoSender(senderQueue));
        DefaultPublisherPipelineSubscriber<Message> subscriber = new DefaultPublisherPipelineSubscriber<>(publisher);

        receiver.subscribe(subscriber);

        Runnable target = () -> {
            System.out.println("START");
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("STOP");
            // TODO: 18.12.2023 !!!
//            for (int i = 0; i < messageQuantity; i++) {
//                PublisherTask<Message> task = service.builder().item(FakerUtil.randomMessage()).build();
//
//                pipeline.offer(new DefaultPipelineBox<>(task));
//            }
//
//            PublisherTask<Message> nTask = service.builder().item(null).build();
//            pipeline.offer(new DefaultPipelineBox<>(nTask));
        };

        return new PublisherDemo(target, pipeline);
    }

    private PublisherDemo(Runnable target, DefaultPublisherPipeline<Message> pipeline) {
        super(target);
        this.pipeline = pipeline;
    }

    @RequiredArgsConstructor
    public static class DemoSender implements Sender<Message> {
        private final BlockingQueue<Message> queue;

        @Override
        public Result<Message> send(Message conversationItem) {
            String code = queue.offer(conversationItem) ? null : "overload";
            return DefaultResultBuilder.<Message>compute(conversationItem, code);
        }
    }
}
