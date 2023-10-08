package ru.multa.entia.conversion.impl.address;

import ru.multa.entia.conversion.api.SimpleFactory;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.function.Function;

public class DefaultAddressFactory implements SimpleFactory<Object, Address> {
    private final Function<Object, Seed> checker;
    private final Function<String, Address> creator;

    public DefaultAddressFactory() {
        this(null, null);
    }

    public DefaultAddressFactory(Function<Object, Seed> checker, Function<String, Address> creator) {
        this.checker = checker == null ? new DefaultAddressValueChecker() : checker;
        this.creator = creator == null ? new DefaultAddressCreator() : creator;
    }

    @Override
    public Result<Address> create(final Object instance, final Object... args) {
        Seed seed = checker.apply(instance);
        return seed == null
                ? DefaultResultBuilder.<Address>ok(creator.apply(String.valueOf(instance)))
                : DefaultResultBuilder.<Address>fail(seed);
    }
}
