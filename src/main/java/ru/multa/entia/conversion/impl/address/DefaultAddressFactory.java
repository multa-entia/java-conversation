package ru.multa.entia.conversion.impl.address;

import ru.multa.entia.conversion.api.Checker;
import ru.multa.entia.conversion.api.SimpleFactory;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.address.AddressCreator;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.Objects;

public class DefaultAddressFactory implements SimpleFactory<Object, Address> {
    private final Checker<Object> checker;
    private final AddressCreator creator;

    public DefaultAddressFactory() {
        this(null, null);
    }

    public DefaultAddressFactory(final Checker<Object> checker, final AddressCreator creator) {
        this.checker = Objects.requireNonNullElse(checker, new DefaultAddressChecker());
        this.creator = Objects.requireNonNullElse(creator, new DefaultAddressCreator());
    }

    @Override
    public Result<Address> create(final Object instance, final Object... args) {
        Seed seed = checker.check(instance);
        return seed == null
                ? DefaultResultBuilder.<Address>ok(creator.create(String.valueOf(instance)))
                : DefaultResultBuilder.<Address>fail(seed);
    }
}
