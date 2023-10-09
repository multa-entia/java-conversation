package ru.multa.entia.conversion.impl.address;

import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.address.AddressCreator;
import ru.multa.entia.conversion.api.address.AddressDecorator;

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
        this.creator = creator == null ? new DefaultAddressCreator() : creator;
        this.template = template == null ? "%s" : template;
    }

    @Override
    public Address decorate(final Address address) {
        return creator.create(String.format(template, address.value()));
    }
}
