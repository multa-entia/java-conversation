package ru.multa.entia.conversion.impl.confirmation;

import ru.multa.entia.conversion.api.SimpleFactory;
import ru.multa.entia.conversion.api.address.Address;
import ru.multa.entia.conversion.api.confirmation.Confirmation;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Function;

public class DefaultConfirmationFactory implements SimpleFactory<Message, Confirmation> {
    // TODO: 05.10.2023 use enum
//    public static final String KEY__FROM_DECORATOR = "default-confirmation-factory.from-decorator";
//    public static final String KEY__CODE = "default-confirmation-factory.code";
//    public static final String KEY__ARGS = "default-confirmation-factory.args";
//
//    public static final String CODE__MESSAGE_NULL = "conversation.factory.confirmation.message-null";
//    public static final String CODE__FIELD_NULL = "conversation.factory.confirmation.id-null";
//
//    public static final String ALIAS__ID = " id";
//    public static final String ALIAS__CONVERSATION = " conversation";
//    public static final String ALIAS__FROM = " from";
//    public static final String ALIAS__TO = " to";


    @Override
    public Result<Confirmation> create(final Message message, final Object... args) {
        return null;
//        Seed seed = checkForNull(message);
//        if (seed == null){
//            seed = checkFieldsForNull(message);
//        }
//
//        return seed == null
//                ? DefaultResultBuilder.<Confirmation>ok(createConfirmation(message, args))
//                : DefaultResultBuilder.<Confirmation>fail(seed);
    }
//
//    private Seed checkForNull(final Message message){
//        return message == null
//                ? new DefaultSeedBuilder<Confirmation>().code(CODE__MESSAGE_NULL).build()
//                : null;
//    }
//
//    private Seed checkFieldsForNull(final Message message){
//        StringBuilder nullFields = new StringBuilder();
//        if (message.id() == null){
//            nullFields.append(ALIAS__ID);
//        }
//        if (message.conversation() == null){
//            nullFields.append(ALIAS__CONVERSATION);
//        }
//        if (message.from() == null){
//            nullFields.append(ALIAS__FROM);
//        }
//        if (message.to() == null){
//            nullFields.append(ALIAS__TO);
//        }
//
//        return nullFields.isEmpty()
//                ? null
//                : new DefaultSeedBuilder<Confirmation>().code(CODE__FIELD_NULL).addLastArgs(nullFields.toString()).build();
//    }
//
//    private Confirmation createConfirmation(final Message message, final Object[] args){
//        return new DefaultConfirmation(
//                message.id(),
//                message.conversation(),
//                new FromDecoratorGetter().apply(args).decorate(message.to()),
//                message.from(),
//                new CodeGetter().apply(args),
//                new ArgsGetter().apply(args)
//        );
//    }
//
//    // TODO: 05.10.2023 move to sep file
//    public interface Decorator {
//        Address decorate(Address value);
//    }
//
//    // TODO: 05.10.2023 move to sep file
//    public static class FromDecoratorGetter implements Function<Object[], Decorator> {
//        @Override
//        public Decorator apply(final Object[] args) {
//            if (args != null){
//                int length = args.length;
//                for (int i = 0; i < length; i++) {
//                    if (KEY__FROM_DECORATOR.equals(args[i]) && i+1 < length){
//                        Object arg = args[i + 1];
//                        for (Type genericInterface : arg.getClass().getGenericInterfaces()) {
//                            if (genericInterface.equals(Decorator.class)){
//                                return (Decorator) arg;
//                            }
//                        }
//                    }
//                }
//            }
//
//            return (s) -> {return s;};
//        }
//    }
//
//    // TODO: 05.10.2023 move to sep file
//    public static class CodeGetter implements Function<Object[], String> {
//        @Override
//        public String apply(final Object[] args) {
//            if (args != null){
//                int length = args.length;
//                for (int i = 0; i < length; i++) {
//                    if (KEY__CODE.equals(args[i]) && i+1 < length && args[i+1].getClass().equals(String.class)){
//                        return (String) args[i+1];
//                    }
//                }
//            }
//
//            return null;
//        }
//    }
//
//    // TODO: 05.10.2023 move to sep file
//    public static class ArgsGetter implements Function<Object[], Object[]>{
//        @Override
//        public Object[] apply(final Object[] args) {
//
//            if (args != null){
//                int start = -1;
//                int length = args.length;
//                for (int i = 0; i < length; i++) {
//                    if (KEY__ARGS.equals(args[i])){
//                        start = i + 1;
//                        break;
//                    }
//                }
//
//                if (start != -1 && start < length){
//                    return Arrays.copyOfRange(args, start, length);
//                }
//            }
//
//            return new Object[0];
//        }
//    }
}
