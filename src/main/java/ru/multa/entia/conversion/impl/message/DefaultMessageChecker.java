package ru.multa.entia.conversion.impl.message;

import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

class DefaultMessageChecker implements Checker<Object> {
    public enum Code {
        IS_NULL;
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.IS_NULL, "checker.message.default.is-null");
    }

    @Override
    public Seed check(final Object instance) {
        return DefaultSeedBuilder.<Object>computeFromCodes(
                () -> {
                    return instance == null ? CR.get(Code.IS_NULL) : null;
                }
        );
    }
}
