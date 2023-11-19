package ru.multa.entia.conversion.impl.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

class DefaultTypeValueChecker implements Checker<Object> {
    @RequiredArgsConstructor
    @Getter
    public enum Code {
        IS_NULL("confirmation.checker.default-confirmation-checker.it-is-null"),
        IS_NOT_STR("confirmation.checker.default-confirmation-checker.it-is-not-str");

        private final String value;
    }

    @Override
    public Seed check(final Object value) {
        return DefaultSeedBuilder.<Object>computeFromCodes(
                () -> {return value == null ? Code.IS_NULL.getValue() : null;},
                () -> {return !value.getClass().equals(String.class) ? Code.IS_NOT_STR.getValue() : null;}
        );
    }
}
