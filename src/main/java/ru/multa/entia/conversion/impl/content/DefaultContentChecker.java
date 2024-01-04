package ru.multa.entia.conversion.impl.content;

import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.conversion.api.value.Value;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

import java.util.Arrays;
import java.util.stream.Collectors;

class DefaultContentChecker implements Checker<Object> {
    public enum Code {
        IS_NULL,
        BAD_PARENT;
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.IS_NULL, "content.checker.default.is-null");
        CR.update(Code.BAD_PARENT, "content.checker.default.bad-parent");
    }

    @Override
    public Seed check(final Object instance) {
        return DefaultSeedBuilder.<Object>computeFromCodes(
                () -> {return instance == null ? CR.get(Code.IS_NULL) : null;},
                () -> {
                    return Arrays.stream(instance.getClass().getInterfaces()).collect(Collectors.toSet()).contains(Value.class)
                            ? null
                            : CR.get(Code.BAD_PARENT);
                }
        );
    }
}
