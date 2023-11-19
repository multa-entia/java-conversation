package ru.multa.entia.conversion.impl.address;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

class DefaultAddressChecker implements Checker<Object> {
    @RequiredArgsConstructor
    @Getter
    public enum Code{
        INSTANCE_IS_NULL("address.checker.default-address-checker.instance-is-null"),
        INSTANCE_IS_NOT_STR("address.checker.default-address-checker.instance-is-not-str"),
        INSTANCE_IS_BLANK("address.checker.default-address-checker.instance-is-blank");

        private final String value;
    }

    @Override
    public Seed check(final Object instance) {
        return DefaultSeedBuilder.<Object>computeFromCodes(
                () -> {return instance == null ? Code.INSTANCE_IS_NULL.getValue() : null;},
                () -> {return instance.getClass().equals(String.class) ? null : Code.INSTANCE_IS_NOT_STR.getValue();},
                () -> {return ((String) instance).isBlank() ? Code.INSTANCE_IS_BLANK.getValue() : null;}
        );
    }
}
