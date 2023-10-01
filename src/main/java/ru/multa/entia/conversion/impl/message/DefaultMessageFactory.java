package ru.multa.entia.conversion.impl.message;

import ru.multa.entia.conversion.api.SimpleFactory;
import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.conversion.impl.content.DefaultContentFactory;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.UUID;
import java.util.function.Function;

public class DefaultMessageFactory implements SimpleFactory<Message> {
    public static final String KEY__IS_REQUEST = "default-message-factory-is-request";
    public static final String KEY__ID = "default-message-factory-id";
    public static final boolean DEFAULT_IS_REQUEST = true;

    private final Function<Object[], Boolean> isRequestGetter = new IsRequestGetter();
    private final Function<Object[], UUID> idGetter = new IdGetter();
    private final SimpleFactory<Content> contentFactory = new DefaultContentFactory();

    @Override
    public Result<Message> create(Object instance, Object... args) {
        UUID id = idGetter.apply(args);
        Boolean isRequest = isRequestGetter.apply(args);
        Result<Content> result = contentFactory.create(instance, args);

        return result.ok()
                ? DefaultResultBuilder.<Message>ok(new DefaultMessage(id, isRequest, result.value()))
                : DefaultResultBuilder.<Message>fail(result.seed());
    }

    public static class IsRequestGetter implements Function<Object[], Boolean>{
        @Override
        public Boolean apply(final Object[] args) {
            if (args != null){
                for (int i = 0; i < args.length; i++) {
                    if (checkArgAsKey(args[i]) && checkPredictedIdArg(args, i+1)){
                        return (Boolean) args[i+1];
                    }
                }
            }

            return DEFAULT_IS_REQUEST;
        }

        private boolean checkArgAsKey(final Object arg){
            return arg != null && arg.getClass().equals(String.class) && arg.equals(KEY__IS_REQUEST);
        }

        private boolean checkPredictedIdArg(final Object[] args, final int idx){
            return args.length > idx && args[idx] != null && args[idx].getClass().equals(Boolean.class);
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
