package ru.multa.entia.conversion.impl.address;

import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

class DefaultAddressChecker implements Checker<Object> {
    public enum Code{
        INSTANCE_IS_NULL,
        INSTANCE_IS_NOT_STR,
        INSTANCE_IS_BLANK;
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.INSTANCE_IS_BLANK, "conversation:address.checker.default:instance-is-null");
        CR.update(Code.INSTANCE_IS_NOT_STR, "conversation:address.checker.default:instance-is-not-str");
        CR.update(Code.INSTANCE_IS_BLANK, "conversation:address.checker.default:instance-is-blank");
    }

    @Override
    public Seed check(final Object instance) {
        return DefaultSeedBuilder.<Object>computeFromCodes(
                () -> {return instance == null ? CR.get(Code.INSTANCE_IS_NULL) : null;},
                () -> {return instance.getClass().equals(String.class) ? null : CR.get(Code.INSTANCE_IS_NOT_STR);},
                () -> {return ((String) instance).isBlank() ? CR.get(Code.INSTANCE_IS_BLANK) : null;}
        );
    }
}
