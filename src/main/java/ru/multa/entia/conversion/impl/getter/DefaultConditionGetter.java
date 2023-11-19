package ru.multa.entia.conversion.impl.getter;

import lombok.RequiredArgsConstructor;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@RequiredArgsConstructor
public class DefaultConditionGetter<T, K> implements Function<Object[], Result<T>> {
    private final K key;
    private final Function<Object, Seed> condition;

    @Override
    public Result<T> apply(final Object[] args) {
        AtomicReference<Object> arg = new AtomicReference<>();
        if (args != null){
            int length = args.length;
            for (int i = 0; i < length; i++) {
                if (checkKey(args[i]) && i+1 < length){
                    arg.set(args[i+1]);
                    break;
                }
            }
        }

        return DefaultResultBuilder.<T>compute(
                () -> {return (T) arg.get();},
                () -> {return condition.apply(arg.get());}
        );
    }

    private boolean checkKey(final Object arg){
        return key != null && key.equals(arg);
    }
}
