package ru.erdenian.studentassistant.netty;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.List;

import ru.erdenian.studentassistant.schedule.LessonRepeat;

public class LessonRepeatDeserializer implements JsonDeserializer<LessonRepeat.ByWeekday> {

    @Override
    public LessonRepeat.ByWeekday deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        @SuppressWarnings("unchecked")
        JsonObject jsonObject = json.getAsJsonObject();
        return new LessonRepeat.ByWeekday(jsonObject.get("weekday").getAsInt(),
                (List<Boolean>) context.deserialize(jsonObject.getAsJsonArray("weeks"), new TypeToken<List<Boolean>>() {
                }.getType()));
    }
}
