package ru.multa.entia.conversion.impl.message;

import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.UUID;
import java.util.function.Function;

class DefaultConversationGetter implements Function<Object[], Result<UUID>> {
    public static final String KEY = "conversation";

    @Override
    public Result<UUID> apply(final Object[] args) {
        if (args != null){
            int length = args.length;
            for (int i = 0; i < length; i++) {
                if (checkKeyArg(args[i]) && checkValueArg(i, args)){
                    return DefaultResultBuilder.<UUID>ok((UUID) args[i+1]);
                }
            }
        }

        return DefaultResultBuilder.<UUID>ok(UUID.randomUUID());
    }

    private boolean checkKeyArg(final Object arg){
        return KEY.equals(arg);
    }

    private boolean checkValueArg(final int idx, final Object[] args){
        return idx+1 < args.length && args[idx+1] != null && args[idx+1].getClass().equals(UUID.class);
    }
}
