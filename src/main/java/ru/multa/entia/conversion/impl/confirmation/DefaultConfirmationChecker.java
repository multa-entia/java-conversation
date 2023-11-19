package ru.multa.entia.conversion.impl.confirmation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

class DefaultConfirmationChecker implements Checker<Message> {
    @RequiredArgsConstructor
    @Getter
    public enum Code {
        INSTANCE_IS_NULL("confirmation.checker.default-confirmation-checker.instance-is-null"),
        FIELD_IS_NULL("confirmation.checker.default-confirmation-checker.instance-is-null");

        private final String value;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Alias {
        ID("[id]"),
        CONVERSATION("[conversation]"),
        FROM("[from]"),
        TO("[to]");

        private final String Value;
    }

    @Override
    public Seed check(final Message instance) {
        return DefaultSeedBuilder.<Object>compute(
                () -> {
                    return instance == null ? DefaultSeedBuilder.<Object>seed(Code.INSTANCE_IS_NULL.getValue()) : null;
                },
                () -> {
                    StringBuilder builder = new StringBuilder();
                    if (instance.id() == null){ builder.append(Alias.ID.getValue()); }
                    if (instance.conversation() == null){ builder.append(Alias.CONVERSATION.getValue()); }
                    if (instance.from() == null){ builder.append(Alias.FROM.getValue()); }
                    if (instance.to() == null){ builder.append(Alias.TO.getValue()); }

                    return builder.isEmpty()
                            ? null
                            : DefaultSeedBuilder.<Object>seed(Code.FIELD_IS_NULL.getValue(), builder.toString());
                }
        );
    }
}
