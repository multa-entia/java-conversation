package ru.multa.entia.conversion.impl.message;

import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.function.Function;

class DefaultContentChecker implements Function<Content, Result<Content>> {
    public static final String CODE = "message.factory.content-checker.content-is-null";

    @Override
    public Result<Content> apply(final Content content) {
        return content != null
                ? DefaultResultBuilder.<Content>ok(content)
                : DefaultResultBuilder.<Content>fail(CODE);
    }
}
