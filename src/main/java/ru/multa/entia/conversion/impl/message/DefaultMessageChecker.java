package ru.multa.entia.conversion.impl.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

class DefaultMessageChecker implements Checker<Object> {
    @RequiredArgsConstructor
    @Getter
    public enum Code {
        IS_NULL("message.checker.default-confirmation-checker.is-null");

        private final String value;
    }

    @Override
    public Seed check(final Object instance) {
        return DefaultSeedBuilder.<Object>computeFromCodes(
                () -> {
                    return instance == null ? Code.IS_NULL.getValue() : null;
                }
        );
    }
}
