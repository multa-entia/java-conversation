package ru.multa.entia.conversion.impl.content;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javatuples.Pair;
import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.content.ContentFactory;
import ru.multa.entia.conversion.api.type.Type;
import ru.multa.entia.conversion.api.type.TypeFactory;
import ru.multa.entia.conversion.api.value.Value;
import ru.multa.entia.conversion.impl.type.DefaultTypeFactory;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DefaultContentFactory implements ContentFactory {
    public static final String CODE__BAD_PARENT = "conversation.factory.content.bad-parent";
    public static final String CODE__BAD_SERIALIZATION = "conversation.factory.content.bad-serialization";

    private final TypeFactory<Object> typeFactory = new DefaultTypeFactory();

    @Override
    public Result<Content> create(final Object instance, final Object... args) {
        Result<Type> result = typeFactory.create(instance, args);
        Seed seed = result.seed();
        seed = checkParent(seed, instance);
        Pair<Seed, String> serializeResult = serialize(seed, instance);

        seed = serializeResult.getValue0();
        return seed == null
                ? DefaultResultBuilder.<Content>ok(new DefaultContent(result.value(), serializeResult.getValue1()))
                : DefaultResultBuilder.<Content>fail(seed);
    }

    private Seed checkParent(final Seed seed, final Object instance) {
        if (seed == null &&
                !Arrays.stream(instance.getClass().getInterfaces()).collect(Collectors.toSet()).contains(Value.class))
        {
            return new DefaultSeedBuilder<Content>().code(CODE__BAD_PARENT).build();
        }

        return seed;
    }

    private Pair<Seed, String> serialize(final Seed seed, final Object instance) {
        if (seed != null){
            return new Pair<>(seed, null);
        }
        try {
            return new Pair<>(null, new ObjectMapper().writeValueAsString(instance));
        } catch (JsonProcessingException ex){
            return new Pair<>(new DefaultSeedBuilder<Content>().code(CODE__BAD_SERIALIZATION).build(), null);
        }
    }
}
