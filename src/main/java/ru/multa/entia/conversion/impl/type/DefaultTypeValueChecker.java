package ru.multa.entia.conversion.impl.type;

import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

class DefaultTypeValueChecker implements Checker<Object> {
    public enum Code {
        IS_NULL,
        IS_NOT_STR;
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.IS_NULL, "conversation:checker.type-value.default:it-is-null");
        CR.update(Code.IS_NOT_STR, "conversation:checker.type-value.default:it-is-not-str");
    }

    @Override
    public Seed check(final Object value) {
        return DefaultSeedBuilder.<Object>computeFromCodes(
                () -> {return value == null ? CR.get(Code.IS_NULL) : null;},
                () -> {return !value.getClass().equals(String.class) ? CR.get(Code.IS_NOT_STR) : null;}
        );
    }
}
