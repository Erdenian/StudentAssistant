package ru.erdenian.gsonguavadeserializers;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * Todo: описание класса.
 *
 * @author Ilya Solovyev
 * @version 0.0.0
 * @since 0.0.0
 */
public class ImmutableSortedSetDeserializer implements JsonDeserializer<ImmutableSortedSet<?>> {

    @Override
    public ImmutableSortedSet<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        @SuppressWarnings("unchecked")
        final TypeToken<ImmutableSortedSet<?>> immutableSortedSetTypeToken = (TypeToken<ImmutableSortedSet<?>>) TypeToken.of(typeOfT);
        final TypeToken<? super ImmutableSortedSet<?>> supertype = immutableSortedSetTypeToken.getSupertype(NavigableSet.class);
        final TreeSet<?> treeSet = context.deserialize(json, supertype.getType());
        return ImmutableSortedSet.copyOf(treeSet);
    }
}
