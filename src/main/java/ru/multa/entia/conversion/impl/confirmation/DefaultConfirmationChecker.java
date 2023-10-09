package ru.multa.entia.conversion.impl.confirmation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

import java.util.function.Function;

class DefaultConfirmationChecker implements Function<Message, Seed> {
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
    public Seed apply(final Message instance) {
        if (instance == null){
            return new DefaultSeedBuilder<Object>().code(Code.INSTANCE_IS_NULL.getValue()).build();
        }

        StringBuilder builder = new StringBuilder();
        if (instance.id() == null){ builder.append(Alias.ID.getValue()); }
        if (instance.conversation() == null){ builder.append(Alias.CONVERSATION.getValue()); }
        if (instance.from() == null){ builder.append(Alias.FROM.getValue()); }
        if (instance.to() == null){ builder.append(Alias.TO.getValue()); }

        if (!builder.isEmpty()){
            return new DefaultSeedBuilder<Object>()
                    .code(Code.FIELD_IS_NULL.getValue())
                    .addLastArgs(builder.toString())
                    .build();
        }

        return null;
    }
}
