package ru.multa.entia.conversion.impl.address;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

import java.util.function.Function;

class DefaultAddressChecker implements Function<Object, Seed> {
    @RequiredArgsConstructor
    @Getter
    public enum Code{
        INSTANCE_IS_NULL("address.checker.default-address-checker.instance-is-null"),
        INSTANCE_IS_NOT_STR("address.checker.default-address-checker.instance-is-not-str"),
        INSTANCE_IS_BLANK("address.checker.default-address-checker.instance-is-blank");

        private final String value;
    }

    @Override
    public Seed apply(final Object instance) {
        String code = null;
        if (instance == null){
            code = Code.INSTANCE_IS_NULL.getValue();
        } else if (!instance.getClass().equals(String.class)) {
            code = Code.INSTANCE_IS_NOT_STR.getValue();
        } else if (((String) instance).isBlank()) {
            code = Code.INSTANCE_IS_BLANK.getValue();
        }

        return code == null ? null : new DefaultSeedBuilder<Object>().code(code).build();
    }
}
