package ru.multa.entia.conversion.impl.pipeline;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.pipeline.PipelineBox;
import ru.multa.entia.conversion.api.pipeline.PipelineReceiver;
import ru.multa.entia.conversion.api.publisher.Publisher;
import ru.multa.entia.results.api.result.Result;

import java.util.UUID;

public class DefaultPipelineReceiver<T extends ConversationItem> implements PipelineReceiver<Publisher<T>> {

    @Override
    public Result<Object> receive(UUID sessionId, PipelineBox<Publisher<T>> box) {
        return null;
    }

    @Override
    public Result<Object> block() {
        return null;
    }

    @Override
    public Result<Object> blockOut(UUID sessionId) {
        return null;
    }
}

//<

//    private static final String DEFAULT_BOX_HANDLER_THREAD_PREFIX = "box-handler-thread-";
//    private static final int BOX_HANDLER_THREAD_LIMIT = 8;

//    private static final Supplier<ExecutorService> DEFAULT_BOX_HANDLER_SUPPLIER = () -> Executors.newFixedThreadPool(
//            BOX_HANDLER_THREAD_LIMIT,
//            new ThreadFactory() {
//                private final AtomicInteger threadNameCounter = new AtomicInteger(0);
//                @Override
//                public Thread newThread(Runnable runnable) {
//                    Thread thread = new Thread(runnable);
//                    thread.setName(DEFAULT_BOX_HANDLER_THREAD_PREFIX + threadNameCounter.incrementAndGet());
//
//                    return thread;
//                }
//            }
//    );

// TODO: 08.11.2023 del
//    @Override
//    public Result<PipelineSubscriber<PublisherTask<T>>> subscribe(final PipelineSubscriber<PublisherTask<T>> subscriber) {
//        log.info("The attempt of subscription: {}", subscriber.getId());
//
//        Code code = Code.SUBSCRIPTION_IF_STARTED;
//        __wLock__.lock();
//        if (!alive){
//            code = subscribers.putIfAbsent(subscriber.getId(), subscriber) == null
//                    ? null
//                    : Code.ALREADY_SUBSCRIBED;
//        }
//        __wLock__.unlock();
//
//        return code == null
//                ? DefaultResultBuilder.<PipelineSubscriber<PublisherTask<T>>>ok(subscriber)
//                : DefaultResultBuilder.<PipelineSubscriber<PublisherTask<T>>>fail(code.getValue());
//    }

// TODO: 08.11.2023 del
//    @Override
//    public Result<PipelineSubscriber<PublisherTask<T>>> unsubscribe(final PipelineSubscriber<PublisherTask<T>> subscriber) {
//        log.info("The attempt of unsubscription: {}", subscriber.getId());
//
//        Code code = Code.UNSUBSCRIPTION_IF_STARTED;
//        __wLock__.lock();
//        if (!alive){
//            code = subscribers.remove(subscriber.getId()) == null
//                    ? Code.NOT_UNSUBSCRIBED
//                    : null;
//        }
//        __wLock__.unlock();
//
//        return code == null
//                ? DefaultResultBuilder.<PipelineSubscriber<PublisherTask<T>>>ok(subscriber)
//                : DefaultResultBuilder.<PipelineSubscriber<PublisherTask<T>>>fail(code.getValue());
//    }
// TODO: 09.11.2023 ???

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

//    @Override
//    public synchronized void dispose() throws InterruptedException {
//        runFlag.set(false);
//        insertStopMessage();
//        messageProcessor.shutdown();
//        messageHandler.awaitTermination(60, TimeUnit.SECONDS);
//    }
//}

