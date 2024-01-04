package ru.multa.entia.conversion.impl.content;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.function.Function;

class DefaultContentSerializer implements Function<Object, Result<String>> {

    public enum Code {
        BAD_ACCESS;
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.BAD_ACCESS, "serializer.content.default.bad-access");
    }

    @Override
    public Result<String> apply(final Object instance) {
        try {
            return DefaultResultBuilder.<String>ok(new ObjectMapper().writeValueAsString(instance));
        } catch (JsonProcessingException ex){
            return DefaultResultBuilder.<String>fail(CR.get(Code.BAD_ACCESS));
        }
    }
}
