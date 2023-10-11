package ru.multa.entia.conversion.impl.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

import java.util.function.Function;

class DefaultTypeValueChecker implements Function<String, Seed> {
    @RequiredArgsConstructor
    @Getter
    public enum Code {
        INSTANCE_IS_NULL("confirmation.checker.default-confirmation-checker.instance-is-null");

        private final String value;
    }

    @Override
    public Seed apply(final String value) {
        return value == null
                ? new DefaultSeedBuilder<Object>().code(Code.INSTANCE_IS_NULL.getValue()).build()
                : null;
    }
}
