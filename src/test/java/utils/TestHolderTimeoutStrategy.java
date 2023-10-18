package utils;

import ru.multa.entia.conversion.api.holder.HolderTimeoutStrategy;
import ru.multa.entia.conversion.api.message.Message;

public class TestHolderTimeoutStrategy implements HolderTimeoutStrategy {
    @Override
    public void execute(final Message message) {

    }

    @Override
    public int getTimeout() {
        return 0;
    }
}
