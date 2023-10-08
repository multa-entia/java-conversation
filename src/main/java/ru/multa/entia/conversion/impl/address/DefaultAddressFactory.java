package ru.multa.entia.conversion.impl.address;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.SimpleFactory;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

public class DefaultAddressFactory implements SimpleFactory<Object, Address> {
    @RequiredArgsConstructor
    @Getter
    public enum Code{
        INSTANCE_IS_NULL("address.factory.default-address-factory.instance-is-null"),
        INSTANCE_IS_NOT_STR("address.factory.default-address-factory.instance-is-not-str"),
        INSTANCE_IS_BLANK("address.factory.default-address-factory.instance-is-blank");

        private final String value;
    }

    @Override
    public Result<Address> create(final Object instance, final Object... args) {
        String code = null;
        if (instance == null){
            code = Code.INSTANCE_IS_NULL.getValue();
        } else if (!instance.getClass().equals(String.class)) {
            code = Code.INSTANCE_IS_NOT_STR.getValue();
        } else if (((String) instance).isBlank()) {
            code = Code.INSTANCE_IS_BLANK.getValue();
        }

        return code == null
                ? DefaultResultBuilder.<Address>ok(new DefaultAddress(String.valueOf(instance)))
                : DefaultResultBuilder.<Address>fail(code);
    }
}
