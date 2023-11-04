package ru.multa.entia.conversion.impl.pipeline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.pipeline.Pipeline;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.pipeline.PipelineSubscriber;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class DefaultPublisherPipeline<T extends ConversationItem> implements Pipeline<PublisherTask<T>> {
    @RequiredArgsConstructor
    @Getter
    public enum Code {
        ALREADY_STARTED("default-publisher-pipeline.already-started"),

        ALREADY_STOPPED("default-publisher-pipeline.already-stopped"),

        SUBSCRIPTION_IF_STARTED("default-publisher-pipeline.subscription-if-started"),
        ALREADY_SUBSCRIBED("default-publisher-pipeline.already-subscribed"),

        UNSUBSCRIPTION_IF_STARTED("default-publisher-pipeline.unsubscription-if-started"),
        NOT_UNSUBSCRIBED("default-publisher-pipeline.not-unsubscribed"),

        OFFER_IF_NOT_STARTED("default-publisher-pipeline.offer-if-not-started"),
        OFFER_QUEUE_IS_FULL("default-publisher-pipeline.offer-queue-is-gull");

        private final String value;
    }

    private static final int DEFAULT_QUEUE_SIZE = 1_000;

    private final AtomicBoolean alive = new AtomicBoolean(false);
    private final ConcurrentMap<UUID, PipelineSubscriber<PublisherTask<T>>> subscribers = new ConcurrentHashMap<>();
    private final BlockingQueue<PipelineBox<PublisherTask<T>>> queue;

    public DefaultPublisherPipeline() {
        this(null);
    }

    public DefaultPublisherPipeline(final BlockingQueue<PipelineBox<PublisherTask<T>>> queue) {
        this.queue = queue == null ? new ArrayBlockingQueue<>(DEFAULT_QUEUE_SIZE) : queue;
    }

    @Override
    public Result<PipelineSubscriber<PublisherTask<T>>> subscribe(final PipelineSubscriber<PublisherTask<T>> subscriber) {
        Code code = Code.SUBSCRIPTION_IF_STARTED;
        if (!alive.get()){
            code = subscribers.putIfAbsent(subscriber.getId(), subscriber) == null
                    ? null
                    : Code.ALREADY_SUBSCRIBED;
        }

        return code == null
                ? DefaultResultBuilder.<PipelineSubscriber<PublisherTask<T>>>ok(subscriber)
                : DefaultResultBuilder.<PipelineSubscriber<PublisherTask<T>>>fail(code.getValue());
    }

    @Override
    public Result<PipelineSubscriber<PublisherTask<T>>> unsubscribe(final PipelineSubscriber<PublisherTask<T>> subscriber) {
        Code code = Code.UNSUBSCRIPTION_IF_STARTED;
        if (!alive.get()){
            code = subscribers.remove(subscriber.getId()) == null
                    ? Code.NOT_UNSUBSCRIBED
                    : null;
        }

        return code == null
                ? DefaultResultBuilder.<PipelineSubscriber<PublisherTask<T>>>ok(subscriber)
                : DefaultResultBuilder.<PipelineSubscriber<PublisherTask<T>>>fail(code.getValue());
    }

    // TODO: 04.11.2023 refact
    @Override
    public Result<PublisherTask<T>> offer(final PipelineBox<PublisherTask<T>> box) {
        Code code = Code.OFFER_IF_NOT_STARTED;
        if (alive.get()){
            code = queue.offer(box) ? null : Code.OFFER_QUEUE_IS_FULL;
        }

        return code == null
                ? DefaultResultBuilder.<PublisherTask<T>>ok(box.value())
                : DefaultResultBuilder.<PublisherTask<T>>fail(code.getValue());
    }

    @Override
    public Result<Object> start() {
        return alive.getAndSet(true)
                ? DefaultResultBuilder.<Object>fail(Code.ALREADY_STARTED.getValue())
                : DefaultResultBuilder.<Object>ok(null);
    }

    @Override
    public Result<Object> stop() {
        return alive.getAndSet(false)
                ? DefaultResultBuilder.<Object>ok(null)
                : DefaultResultBuilder.<Object>fail(Code.ALREADY_STOPPED.getValue());
    }

    @Override
    public Result<Object> stopWithoutClearing() {
        return null;
    }
}

//<

//package ru.otus.kasymbekovPN.zuiNotesMS.messageSystem;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import ru.otus.kasymbekovPN.zuiNotesMS.messageSystem.client.MSClient;
//import ru.otus.kasymbekovPN.zuiNotesMS.messageSystem.client.service.MsClientService;
//
//import java.util.Optional;
//import java.util.concurrent.*;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicInteger;
//
//@Service
//public class MessageSystemImpl implements MessageSystem {
//
//    private static final Logger logger = LoggerFactory.getLogger(MessageSystemImpl.class);
//
//    private static final int MESSAGE_QUEUE_SIZE = 1_000;
//    private static final int MESSAGE_HANDLER_THREAD_LIMIT = 2;
//
//    private final AtomicBoolean runFlag = new AtomicBoolean(true);
//    private final MsClientService msClientService;
//    private final BlockingQueue<Message> messageQueue = new ArrayBlockingQueue<>(MESSAGE_QUEUE_SIZE);
//    private final ExecutorService messageProcessor = Executors.newSingleThreadExecutor(
//            runnable -> {
//                Thread thread = new Thread(runnable);
//                thread.setName("message-processor-thread");
//                return thread;
//            }
//    );
//    private final ExecutorService messageHandler = Executors.newFixedThreadPool(
//            MESSAGE_HANDLER_THREAD_LIMIT,
//            new ThreadFactory() {
//                private final AtomicInteger threadNameCounter = new AtomicInteger(0);
//                @Override
//                public Thread newThread(Runnable runnable) {
//                    Thread thread = new Thread(runnable);
//                    thread.setName("message-handler-thread-" + threadNameCounter.incrementAndGet());
//                    return  thread;
//                }
//            }
//    );
//
//    public MessageSystemImpl(MsClientService msClientService) {
//        this.msClientService = msClientService;
//        messageProcessor.submit(this::messageProcessor);
//    }
//
//    private void messageProcessor(){
//        logger.info("messageProcessor started");
//        while (runFlag.get())
//        {
//            try{
//                Message message = messageQueue.take();
//                if (message == Message.getVoidMessage()){
//                    logger.info("Received the stop message");
//                } else {
//                    Optional<MSClient> optClientTo = msClientService.get(message.getToUrl());
//                    if (optClientTo.isPresent()){
//                        messageHandler.submit(
//                                () -> handlerMessage(optClientTo.get(), message)
//                        );
//                    } else {
//                        logger.warn("Client not found");
//                    }
//                }
//            } catch (InterruptedException ex){
//                logger.error(ex.getMessage(), ex);
//                Thread.currentThread().interrupt();
//            } catch (Exception ex){
//                logger.error(ex.getMessage(), ex);
//            }
//        }
//
//        messageHandler.submit(this::messageHandlerShutdown);
//        logger.info("messageProcessor finished");
//    }
//
//    private void messageHandlerShutdown(){
//        messageHandler.shutdown();
//        logger.info("messageHandler has been shut down");
//    }
//
//    private void handlerMessage(MSClient msClient, Message message){
//        try{
//            msClient.handle(message);
//        } catch(Exception ex){
//            logger.error(ex.getMessage(), ex);
//            logger.error("message : {}", message);
//        }
//    }
//
//    private void insertStopMessage() throws InterruptedException {
//        boolean result = messageQueue.offer(Message.getVoidMessage());
//        while (!result){
//            Thread.sleep(100);
//            result = messageQueue.offer(Message.getVoidMessage());
//        }
//    }
//
//    @Override
//    public synchronized boolean newMessage(Message message) {
//        if (runFlag.get()){
//            return messageQueue.offer(message);
//        } else {
//            logger.warn("MS is being shutting down... rejected : {}", message);
//            return false;
//        }
//    }
//
//    @Override
//    public synchronized void dispose() throws InterruptedException {
//        runFlag.set(false);
//        insertStopMessage();
//        messageProcessor.shutdown();
//        messageHandler.awaitTermination(60, TimeUnit.SECONDS);
//    }
//}

