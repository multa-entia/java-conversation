package ru.multa.entia.conversion.impl.message;

import lombok.RequiredArgsConstructor;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
class DefaultGetter<T> implements Function<Object[], Result<T>> {
    private final DefaultMessageFactory.Keys key;
    private final Supplier<T> defaultSupplier;

    @Override
    public Result<T> apply(final Object[] args) {
        T defaultValue = defaultSupplier.get();
        if (args != null){
            int length = args.length;
            for (int i = 0; i < length; i++) {
                if (checkKey(args[i]) && checkValue(i+1, args, defaultValue.getClass())){
                    return DefaultResultBuilder.<T>ok((T) args[i+1]);
                }
            }
        }

        return DefaultResultBuilder.<T>ok(defaultValue);
    }

    private boolean checkKey(final Object arg){
        return key != null && key.equals(arg);
    }

    private boolean checkValue(final int idx, final Object[] args, final Class<?> defaultType){
        int length = args.length;
        return idx < length && defaultType.equals(args[idx].getClass());
    }
}
