package ru.multa.entia.conversion.impl.address;

import ru.multa.entia.conversion.api.address.Address;

import java.util.function.Function;

class DefaultAddressCreator implements Function<String, Address> {

    @Override
    public Address apply(final String value) {
        return new DefaultAddress(value);
    }
}
