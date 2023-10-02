package ru.multa.entia.conversion.impl.address;

import ru.multa.entia.conversion.api.SimpleFactory;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

public class DefaultAddressFactory implements SimpleFactory<Object, Address> {
    public static final String CODE__IS_NULL = "conversation.factory.address.instance-is-null";
    public static final String CODE__IS_NOT_STRING = "conversation.factory.address.instance-is-not-string";
    public static final String CODE__IS_BLANK = "conversation.factory.address.instance-is-empty";


    @Override
    public Result<Address> create(Object instance, Object... args) {
        String code = null;
        if (instance == null){
            code = CODE__IS_NULL;
        } else if (!instance.getClass().equals(String.class)) {
            code = CODE__IS_NOT_STRING;
        } else if (((String) instance).isBlank()) {
            code = CODE__IS_BLANK;
        }

        return code == null
                ? DefaultResultBuilder.<Address>ok(new DefaultAddress((String) instance))
                : DefaultResultBuilder.<Address>fail(code);
    }
}
