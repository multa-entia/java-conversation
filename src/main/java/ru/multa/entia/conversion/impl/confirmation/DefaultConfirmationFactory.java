package ru.multa.entia.conversion.impl.confirmation;

import ru.multa.entia.conversion.api.SimpleFactory;
import ru.multa.entia.conversion.api.confirmation.Confirmation;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.results.api.result.Result;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Function;

public class DefaultConfirmationFactory implements SimpleFactory<Message, Confirmation> {
    public static final String KEY__FROM_DECORATOR = "default-confirmation=factory.from-decorator";
    public static final String KEY__CODE = "default-confirmation=factory.code";
    public static final String KEY__ARGS = "default-confirmation=factory.args";

    @Override
    public Result<Confirmation> create(final Message message, final Object... args) {
        return null;
    }

    public interface Decorator {
        String decorate(String value);
    }

    public static class FromDecoratorGetter implements Function<Object[], Decorator> {
        @Override
        public Decorator apply(final Object[] args) {
            if (args != null){
                int length = args.length;
                for (int i = 0; i < length; i++) {
                    if (KEY__FROM_DECORATOR.equals(args[i]) && i+1 < length){
                        Object arg = args[i + 1];
                        for (Type genericInterface : arg.getClass().getGenericInterfaces()) {
                            if (genericInterface.equals(Decorator.class)){
                                return (Decorator) arg;
                            }
                        }
                    }
                }
            }

            return (s) -> {return s;};
        }
    }

    public static class CodeGetter implements Function<Object[], String> {
        @Override
        public String apply(final Object[] args) {
            if (args != null){
                int length = args.length;
                for (int i = 0; i < length; i++) {
                    if (KEY__CODE.equals(args[i]) && i+1 < length && args[i+1].getClass().equals(String.class)){
                        return (String) args[i+1];
                    }
                }
            }

            return null;
        }
    }

    public static class ArgsGetter implements Function<Object[], Object[]>{
        @Override
        public Object[] apply(final Object[] args) {

            if (args != null){
                int start = -1;
                int length = args.length;
                for (int i = 0; i < length; i++) {
                    if (KEY__ARGS.equals(args[i])){
                        start = i + 1;
                        break;
                    }
                }

                if (start != -1 && start < length){
                    return Arrays.copyOfRange(args, start, length);
                }
            }

            return new Object[0];
        }
    }
}
