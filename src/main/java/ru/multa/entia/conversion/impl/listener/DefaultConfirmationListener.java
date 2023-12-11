package ru.multa.entia.conversion.impl.listener;

import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.confirmation.Confirmation;
import ru.multa.entia.conversion.api.holder.Holder;
import ru.multa.entia.conversion.api.holder.HolderItem;
import ru.multa.entia.conversion.api.listener.Listener;
import ru.multa.entia.conversion.api.listener.ListenerTask;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

@RequiredArgsConstructor
public class DefaultConfirmationListener implements Listener<Confirmation> {
    private final Holder holder;

    @Override
    public Result<Confirmation> listen(final ListenerTask<Confirmation> task) {
        Confirmation confirmation = task.item();
        Result<HolderItem> result = holder.release(confirmation);
        return result.ok()
                ? DefaultResultBuilder.<Confirmation>ok(confirmation)
                : DefaultResultBuilder.<Confirmation>fail(result.seed());
    }
}
