package ru.multa.entia.conversion.impl.getter;

import lombok.RequiredArgsConstructor;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.Arrays;
import java.util.function.Function;

@RequiredArgsConstructor
public class DefaultArgsGetter<K> implements Function<Object[], Result<Object[]>> {
    private final K key;

    @Override
    public Result<Object[]> apply(final Object[] args) {
        if (args != null){
            int length = args.length;
            int start = -1;
            for (int i = 0; i < length; i++) {
                if (key != null && key.equals(args[i]) && i + 1 < length){
                    start = i+1;
                    break;
                }
            }

            if (start != -1){
                return DefaultResultBuilder.<Object[]>ok(Arrays.copyOfRange(args, start, length));
            }
        }

        return DefaultResultBuilder.<Object[]>ok(new Object[0]);
    }
}
