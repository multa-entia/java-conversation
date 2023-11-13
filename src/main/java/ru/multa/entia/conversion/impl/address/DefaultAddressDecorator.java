package ru.multa.entia.conversion.impl.address;

import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.address.AddressCreator;
import ru.multa.entia.conversion.api.address.AddressDecorator;

import java.util.Objects;

public class DefaultAddressDecorator implements AddressDecorator {
    private final AddressCreator creator;
    private final String template;

    public DefaultAddressDecorator() {
        this(null, null);
    }

    public DefaultAddressDecorator(final AddressCreator creator){
        this(creator, null);
    }

    public DefaultAddressDecorator(final String template){
        this(null, template);
    }

    public DefaultAddressDecorator(final AddressCreator creator, final String template) {
        this.creator = Objects.requireNonNullElse(creator, new DefaultAddressCreator());
        this.template = Objects.requireNonNullElse(template, "%s");
    }

    @Override
    public Address decorate(final Address address) {
        return creator.create(String.format(template, address.value()));
    }
}
