package ru.multa.entia.conversion.impl.content;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.javatuples.Pair;
import ru.multa.entia.conversion.api.SimpleFactory;
import ru.multa.entia.conversion.api.content.Content;
import ru.multa.entia.conversion.api.type.Type;
import ru.multa.entia.conversion.api.value.Value;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.impl.seed.DefaultSeedBuilder;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DefaultContentFactory implements SimpleFactory<Object, Content> {
    // TODO: 12.10.2023 del
//    // TODO: 05.10.2023 use enum
//    public static final String CODE__BAD_PARENT = "conversation.factory.content.bad-parent";
//    public static final String CODE__BAD_SERIALIZATION = "conversation.factory.content.bad-serialization";
//
//    // TODO: 05.10.2023 through ctor
////    private final SimpleFactory<Object, Type> typeFactory = new DefaultTypeFactory();
//    private final SimpleFactory<Object, Type> typeFactory = null;

//    private final TypeFactory typeFactory;

    @Override
    public Result<Content> create(final Object instance, final Object... args) {

        throw new RuntimeException("");

        // TODO: 12.10.2023 del
//        Result<Type> result = typeFactory.create(instance, args);
//        Seed seed = result.seed();
//        seed = checkParent(seed, instance);
//        Pair<Seed, String> serializeResult = serialize(seed, instance);
//
//        seed = serializeResult.getValue0();
//        return seed == null
//                ? DefaultResultBuilder.<Content>ok(new DefaultContent(result.value(), serializeResult.getValue1()))
//                : DefaultResultBuilder.<Content>fail(seed);
    }


    // TODO: 12.10.2023 del
//    private Seed checkParent(final Seed seed, final Object instance) {
//        if (seed == null &&
//                !Arrays.stream(instance.getClass().getInterfaces()).collect(Collectors.toSet()).contains(Value.class))
//        {
//            return new DefaultSeedBuilder<Content>().code(CODE__BAD_PARENT).build();
//        }
//
//        return seed;
//    }
//
//    private Pair<Seed, String> serialize(final Seed seed, final Object instance) {
//        if (seed != null){
//            return new Pair<>(seed, null);
//        }
//        try {
//            return new Pair<>(null, new ObjectMapper().writeValueAsString(instance));
//        } catch (JsonProcessingException ex){
//            return new Pair<>(new DefaultSeedBuilder<Content>().code(CODE__BAD_SERIALIZATION).build(), null);
//        }
//    }
}
