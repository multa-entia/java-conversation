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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

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
    private static final String DEFAULT_BOX_PROCESSOR_THREAD_NAME = "box-processor-thread";
    private static final String DEFAULT_BOX_HANDLER_THREAD_PREFIX = "box-handler-thread-";
    private static final int BOX_HANDLER_THREAD_LIMIT = 8;
    private static final Supplier<ExecutorService> DEFAULT_BOX_PROCESSOR_SUPPLIER = () -> Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName(DEFAULT_BOX_PROCESSOR_THREAD_NAME);

        return thread;
    });
    private static final Supplier<ExecutorService> DEFAULT_BOX_HANDLER_SUPPLIER = () -> Executors.newFixedThreadPool(
            BOX_HANDLER_THREAD_LIMIT,
            new ThreadFactory() {
                private final AtomicInteger threadNameCounter = new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable runnable) {
                    Thread thread = new Thread(runnable);
                    thread.setName(DEFAULT_BOX_HANDLER_THREAD_PREFIX + threadNameCounter.incrementAndGet());

                    return thread;
                }
            }
    );

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock __wLock__ = lock.writeLock();
    private final Lock __rLock__ = lock.readLock();

    private final Map<UUID, PipelineSubscriber<PublisherTask<T>>> subscribers = new HashMap<>();
    private final BlockingQueue<PipelineBox<PublisherTask<T>>> queue;
    private final Supplier<ExecutorService> boxProcessorSupplier;
    private final Supplier<ExecutorService> boxHandlerSupplier;

    private boolean alive = false;
    private ExecutorService boxProcessor;
    private ExecutorService boxHandler;
    private UUID sessionId;

    public DefaultPublisherPipeline() {
        this(null, null, null);
    }

    public DefaultPublisherPipeline(final BlockingQueue<PipelineBox<PublisherTask<T>>> queue,
                                    final Supplier<ExecutorService> boxProcessorSupplier,
                                    final Supplier<ExecutorService> boxHandlerSupplier) {
        // TODO: 04.11.2023 use requireNonNullElseGet in other places
        this.queue = Objects.requireNonNullElseGet(queue, () -> {return new ArrayBlockingQueue<>(DEFAULT_QUEUE_SIZE);});
        this.boxProcessorSupplier = Objects.requireNonNullElseGet(boxProcessorSupplier, () -> DEFAULT_BOX_PROCESSOR_SUPPLIER);
        this.boxHandlerSupplier = Objects.requireNonNullElseGet(boxHandlerSupplier, () -> DEFAULT_BOX_HANDLER_SUPPLIER);
    }

    @Override
    public Result<PipelineSubscriber<PublisherTask<T>>> subscribe(final PipelineSubscriber<PublisherTask<T>> subscriber) {
        log.info("The attempt of subscription: {}", subscriber.getId());

        Code code = Code.SUBSCRIPTION_IF_STARTED;
        __wLock__.lock();
        if (!alive){
            code = subscribers.putIfAbsent(subscriber.getId(), subscriber) == null
                    ? null
                    : Code.ALREADY_SUBSCRIBED;
        }
        __wLock__.unlock();

        return code == null
                ? DefaultResultBuilder.<PipelineSubscriber<PublisherTask<T>>>ok(subscriber)
                : DefaultResultBuilder.<PipelineSubscriber<PublisherTask<T>>>fail(code.getValue());
    }

    @Override
    public Result<PipelineSubscriber<PublisherTask<T>>> unsubscribe(final PipelineSubscriber<PublisherTask<T>> subscriber) {
        log.info("The attempt of unsubscription: {}", subscriber.getId());

        Code code = Code.UNSUBSCRIPTION_IF_STARTED;
        __wLock__.lock();
        if (!alive){
            code = subscribers.remove(subscriber.getId()) == null
                    ? Code.NOT_UNSUBSCRIBED
                    : null;
        }
        __wLock__.unlock();

        return code == null
                ? DefaultResultBuilder.<PipelineSubscriber<PublisherTask<T>>>ok(subscriber)
                : DefaultResultBuilder.<PipelineSubscriber<PublisherTask<T>>>fail(code.getValue());
    }

    @Override
    public Result<PublisherTask<T>> offer(final PipelineBox<PublisherTask<T>> box) {
        log.info("The attempt of offer: {}", box.value());

        Code code = Code.OFFER_IF_NOT_STARTED;
        __wLock__.lock();
        if (alive){
            code = queue.offer(box) ? null : Code.OFFER_QUEUE_IS_FULL;
        }
        __wLock__.unlock();

        return code == null
                ? DefaultResultBuilder.<PublisherTask<T>>ok(box.value())
                : DefaultResultBuilder.<PublisherTask<T>>fail(code.getValue());
    }

    @Override
    public Result<Object> start() {
        log.info("The attempt of starting");

        __wLock__.lock();
        if (alive) {
            __wLock__.unlock();

            return DefaultResultBuilder.<Object>fail(Code.ALREADY_STARTED.getValue());
        }

        alive = true;
        sessionId = UUID.randomUUID();
        for (Map.Entry<UUID, PipelineSubscriber<PublisherTask<T>>> entry : subscribers.entrySet()) {
            entry.getValue().blockOut(sessionId);
        }

        boxProcessor = boxProcessorSupplier.get();
        boxHandler = boxHandlerSupplier.get();
        boxProcessor.submit(this::processBoxes);
        __wLock__.unlock();

        return DefaultResultBuilder.<Object>ok(null);
    }

    @Override
    public Result<Object> stop() {
        log.info("The attempt of stopping");
        return reset(false);
    }

    @Override
    public Result<Object> stopWithClearing() {
        log.info("The attempt of stopping with clearing");
        return reset(true);
    }

    private Result<Object> reset(final boolean clearQueue){
        __wLock__.lock();
        if (!alive){
            __wLock__.unlock();

            return DefaultResultBuilder.<Object>fail(Code.ALREADY_STOPPED.getValue());
        }

        for (Map.Entry<UUID, PipelineSubscriber<PublisherTask<T>>> entry : subscribers.entrySet()) {
            entry.getValue().block();
        }
        alive = false;
        sessionId = null;
        boxProcessor.shutdown();
        boxHandler.shutdown();
        boxProcessor = null;
        boxHandler = null;
        if (clearQueue){
            queue.clear();
        }
        __wLock__.unlock();

        return DefaultResultBuilder.<Object>ok(null);
    }

    private void processBoxes(){
        // TODO: 06.11.2023 !!!
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

//    @Override
//    public synchronized void dispose() throws InterruptedException {
//        runFlag.set(false);
//        insertStopMessage();
//        messageProcessor.shutdown();
//        messageHandler.awaitTermination(60, TimeUnit.SECONDS);
//    }
//}

