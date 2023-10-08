package ru.multa.entia.conversion.impl.address;

import ru.multa.entia.conversion.api.SimpleFactory;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.address.AddressCreator;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.function.Function;

public class DefaultAddressFactory implements SimpleFactory<Object, Address> {
    private final Function<Object, Seed> checker;
    private final AddressCreator creator;

    public DefaultAddressFactory() {
        this(null, null);
    }

    public DefaultAddressFactory(Function<Object, Seed> checker, AddressCreator creator) {
        this.checker = checker == null ? new DefaultAddressChecker() : checker;
        this.creator = creator == null ? new DefaultAddressCreator() : creator;
    }

    @Override
    public Result<Address> create(final Object instance, final Object... args) {
        Seed seed = checker.apply(instance);
        return seed == null
                ? DefaultResultBuilder.<Address>ok(creator.create(String.valueOf(instance)))
                : DefaultResultBuilder.<Address>fail(seed);
    }
}
