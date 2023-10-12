package ru.multa.entia.conversion.impl.content;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.conversion.api.value.Value;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

import java.util.Arrays;
import java.util.stream.Collectors;

class DefaultContentChecker implements Checker<Object> {
    @RequiredArgsConstructor
    @Getter
    public enum Code {
        IS_NULL("content.checker.default-confirmation-checker.is-null"),
        BAD_PARENT("content.checker.default-confirmation-checker.bad-parent");

        private final String value;
    }

    @Override
    public Seed check(final Object instance) {
        if (instance == null){
            return new DefaultSeedBuilder<Object>().code(Code.IS_NULL.getValue()).build();
        }

        if (!Arrays.stream(instance.getClass().getInterfaces()).collect(Collectors.toSet()).contains(Value.class)){
            return new DefaultSeedBuilder<Object>().code(Code.BAD_PARENT.getValue()).build();
        }

        return null;
    }
}
