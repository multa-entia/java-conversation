package ru.multa.entia.conversion.impl.content;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.function.Function;

class DefaultContentSerializer implements Function<Object, Result<String>> {
    @RequiredArgsConstructor
    @Getter
    public enum Code {
        BAD_ACCESS("default-content-serializer.bad-access");

        private final String value;
    }

    @Override
    public Result<String> apply(final Object instance) {
        try {
            return DefaultResultBuilder.<String>ok(new ObjectMapper().writeValueAsString(instance));
        } catch (JsonProcessingException ex){
            return DefaultResultBuilder.<String>fail(Code.BAD_ACCESS.getValue());
        }
    }
}
