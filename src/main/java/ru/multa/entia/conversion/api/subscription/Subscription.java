package ru.multa.entia.conversion.api.subscription;

import ru.multa.entia.results.api.result.Result;

public interface Subscription<S> {
    Result<S> subscribe(S subscriber);
    Result<S> unsubscribe(S subscriber);
}
