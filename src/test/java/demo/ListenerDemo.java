package demo;

import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.listener.ListenerStrategy;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.impl.listener.DefaultMessageListener;
import ru.multa.entia.conversion.impl.pipeline.*;
import ru.multa.entia.conversion.impl.pipeline.subscriber.DefaultPipelineListenerSubscriber;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ListenerDemo extends Thread{
    private final DefaultListenerPipeline<Message> pipeline;

    public static ListenerDemo create(final ArrayBlockingQueue<PipelineBox<ListenerTask<Message>>> listenerQueue,
                                      final ArrayBlockingQueue<Message> strategyQueue) {
        DefaultPipelineListenerReceiver<Message> receiver = new DefaultPipelineListenerReceiver<>();
        DefaultListenerPipeline<Message> pipeline = new DefaultListenerPipeline<Message>(listenerQueue, receiver);
        pipeline.start();

        DefaultMessageListener listener = new DefaultMessageListener(new DemoListenerStrategy(strategyQueue));
        DefaultPipelineListenerSubscriber<Message> subscriber = new DefaultPipelineListenerSubscriber<>(listener);

        receiver.subscribe(subscriber);

        Runnable target = () -> {
            System.out.println("START");
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("STOP");
        };

        return new ListenerDemo(target, pipeline);
    }

    private ListenerDemo(final Runnable target, final DefaultListenerPipeline<Message> pipeline) {
        super(target);
        this.pipeline = pipeline;
    }

    @RequiredArgsConstructor
    public static class DemoListenerStrategy implements ListenerStrategy<Message> {
        private final BlockingQueue<Message> queue;

        @Override
        public Result<Message> execute(Message item) {
            String code = queue.offer(item) ? null : "overload";
            return DefaultResultBuilder.<Message>compute(item, code);
        }
    }
}
