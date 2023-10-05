package ru.multa.entia.conversion.impl.message;

import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.UUID;
import java.util.function.Function;

class DefaultConfirmGetter implements Function<Object[], Result<Boolean>> {

    @Override
    public Result<Boolean> apply(Object[] args) {
        if (args != null){
            int length = args.length;
            for (int i = 0; i < length; i++) {
                if (checkKeyArg(args[i]) && checkValueArg(i, args)){
                    return DefaultResultBuilder.<Boolean>ok((Boolean) args[i+1]);
                }
            }
        }

        return DefaultResultBuilder.<Boolean>ok(false);
    }

    private boolean checkKeyArg(final Object arg){
        return DefaultMessageFactory.Keys.CONFIRM.equals(arg);
    }

    private boolean checkValueArg(final int idx, final Object[] args){
        return idx+1 < args.length && args[idx+1] != null && args[idx+1].getClass().equals(Boolean.class);
    }
}
