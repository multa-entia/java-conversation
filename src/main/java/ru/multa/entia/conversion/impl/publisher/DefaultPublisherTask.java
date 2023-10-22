package ru.multa.entia.conversion.impl.publisher;

import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.holder.HolderReleaseStrategy;
import ru.multa.entia.conversion.api.holder.HolderTimeoutStrategy;
import ru.multa.entia.conversion.api.publisher.PublisherTask;

record DefaultPublisherTask<T extends ConversationItem>(T item,
                                                        HolderTimeoutStrategy timeoutStrategy,
                                                        HolderReleaseStrategy releaseStrategy) implements PublisherTask<T> {
}
