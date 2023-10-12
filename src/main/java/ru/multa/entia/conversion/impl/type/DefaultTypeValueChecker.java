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
        String code = null;
        if (value == null){
            code = Code.IS_NULL.getValue();
        } else if (!value.getClass().equals(String.class)) {
            code = Code.IS_NOT_STR.getValue();
        }

        return code == null
                ? null
                : new DefaultSeedBuilder<Object>().code(code).build();
    }
}
