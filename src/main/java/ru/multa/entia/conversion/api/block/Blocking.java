package ru.multa.entia.conversion.api.block;

import ru.multa.entia.results.api.result.Result;

public interface Blocking<K, R> {
    Result<R> block();
    Result<R> blockOut(K key);
}
