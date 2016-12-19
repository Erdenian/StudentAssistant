package ru.erdenian.gsonguavadeserializers;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Todo: описание класса.
 *
 * @author Ilya Solovyev
 * @version 0.0.0
 * @since 0.0.0
 */
public class ImmutableListDeserializer implements JsonDeserializer<ImmutableList<?>> {

    @Override
    public ImmutableList<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        @SuppressWarnings("unchecked")
        final TypeToken<ImmutableList<?>> immutableListTypeToken = (TypeToken<ImmutableList<?>>) TypeToken.of(typeOfT);
        final TypeToken<? super ImmutableList<?>> supertype = immutableListTypeToken.getSupertype(List.class);
        final List<?> list = context.deserialize(json, supertype.getType());
        return ImmutableList.copyOf(list);
    }
}
