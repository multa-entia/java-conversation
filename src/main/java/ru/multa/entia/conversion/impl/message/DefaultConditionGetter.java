package ru.multa.entia.conversion.impl.message;

import lombok.RequiredArgsConstructor;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.function.Function;

// TODO: 07.10.2023 move to getter
@RequiredArgsConstructor
class DefaultConditionGetter<T> implements Function<Object[], Result<T>> {
    private final DefaultMessageFactory.Keys key;
    private final Function<Object, Seed> valueChecker;

    @Override
    public Result<T> apply(final Object[] args) {
        Object arg = null;
        if (args != null){
            int length = args.length;
            for (int i = 0; i < length; i++) {
                if (checkKey(args[i]) && i+1 < length){
                    arg = args[i+1];
                    break;
                }
            }
        }

        Seed seed = valueChecker.apply(arg);
        return seed == null
                ? DefaultResultBuilder.<T>ok((T) arg)
                : DefaultResultBuilder.<T>fail(seed);
    }

    private boolean checkKey(final Object arg){
        return key != null && key.equals(arg);
    }
}
