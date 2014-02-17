package at.yawk.fimfiction.json;

import at.yawk.fimfiction.data.*;
import com.google.common.base.Preconditions;
import com.google.gson.*;
import java.util.Date;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Helper class to serialize Bundle instances to JSON objects.
 *
 * @author Jonas Konrad (yawkat)
 */
public class Serializer {
    static final Gson GSON = new Gson();

    @Nonnull
    public <K extends Key> JsonObject serializeBundle(@Nonnull Bundle<?, K> bundle) {
        Preconditions.checkNotNull(bundle);

        JsonObject result = new JsonObject();
        for (K key : bundle.getSetKeys()) {
            result.add(key.getId(), serializeValue(key.getType(), bundle.get(key)));
        }
        return result;
    }

    @Nonnull
    private JsonElement serializeValue(@Nonnull ValueType type, @Nonnull Object o) {
        JsonElement simple = serializeSimpleValue(type, o);

        if (simple != null) { return simple; }

        if (Bundle.class.isAssignableFrom(type.getType())) { return serializeBundle((Bundle<?, ?>) o); }

        throw new UnsupportedOperationException(type + " is not supported for serialization.");
    }

    @Nullable
    private JsonElement serializeSimpleValue(@Nonnull ValueType type, @Nonnull Object o) {
        if (type.isContainer()) {
            assert type.getElementType() != null;
            if (type.getType() == Optional.class) {
                return ((Optional) o).exists() ?
                        serializeValue(type.getElementType(), ((Optional) o).get()) :
                        JsonNull.INSTANCE;
            } else if (Iterable.class.isAssignableFrom(type.getType())) {
                JsonArray array = new JsonArray();
                for (Object element : (Iterable<?>) o) {
                    array.add(serializeValue(type.getElementType(), element));
                }
                return array;
            }
        } else {
            if (type == ValueType.BOOLEAN) { return new JsonPrimitive((Boolean) o); }
            if (type == ValueType.NUMBER) { return new JsonPrimitive((Number) o); }
            if (type == ValueType.URL) { return new JsonPrimitive(o.toString()); }
            if (type == ValueType.STRING) { return new JsonPrimitive((String) o); }
            if (type == ValueType.DATE) {
                return new JsonPrimitive(Deserializer.createIsoDateFormat().format((Date) o));
            }
            if (type == ValueType.FORMATTED_STRING) {
                return new JsonPrimitive(((FormattedString) o).buildFormattedText(FormattedString.Markup.HTML));
            }
            if (Identifiable.class.isAssignableFrom(type.getType())) {
                return new JsonPrimitive(((Identifiable) o).getId());
            }
        }
        return null;
    }
}
