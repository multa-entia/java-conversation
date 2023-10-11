package ru.multa.entia.conversion.api;

import ru.multa.entia.results.api.seed.Seed;

public interface Checker<T> {
    Seed check(T instance);
}
