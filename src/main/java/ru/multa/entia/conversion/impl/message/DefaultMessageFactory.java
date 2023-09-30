package ru.multa.entia.conversion.impl.message;

import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.api.message.MessageFactory;
import ru.multa.entia.results.api.result.Result;

import java.util.UUID;
import java.util.function.Function;

public class DefaultMessageFactory implements MessageFactory {
    public static final String KEY__IS_REQUEST = "default-message-factory-is-request";
    public static final String KEY__ID = "default-message-factory-id";

    @Override
    public Result<Message> create(Object instance, Object... args) {
        return null;
    }

    public static class IsRequestGetter implements Function<Object[], Boolean>{
        @Override
        public Boolean apply(final Object[] args) {
            return null;
        }
    }

    public static class IdGetter implements Function<Object[], UUID> {
        @Override
        public UUID apply(final Object[] args) {
            if (args != null){
                for (int i = 0; i < args.length; i++) {
                    if (checkArgAsKey(args[i]) && checkPredictedIdArg(args, i+1)){
                        return (UUID) args[i+1];
                    }
                }
            }

            return UUID.randomUUID();
        }

        private boolean checkArgAsKey(final Object arg){
            return arg != null && arg.getClass().equals(String.class) && arg.equals(KEY__ID);
        }

        private boolean checkPredictedIdArg(final Object[] args, final int idx){
            return args.length > idx && args[idx] != null && args[idx].getClass().equals(UUID.class);
        }
    }
}
