package ru.multa.entia.conversion.impl.address;

import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.address.AddressCreator;

class DefaultAddressCreator implements AddressCreator {

    @Override
    public Address create(String value) {
        return new DefaultAddress(value);
    }
}
