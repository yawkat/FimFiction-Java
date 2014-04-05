package at.yawk.fimfiction.json;

import at.yawk.fimfiction.core.FormattedStringParser;
import at.yawk.fimfiction.data.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.xml.sax.SAXException;

/**
 * Helper class to deserialize JsonObject into Bundle instances of a specific type.
 *
 * @author Jonas Konrad (yawkat)
 */
public class Deserializer {
    @Nonnull
    public <B extends Bundle<B, K>, K extends Key> B deserializeBundle(@Nonnull JsonObject object,
                                                                       @Nonnull Class<B> type)
            throws ParseException, SAXException {
        Preconditions.checkNotNull(object);
        Preconditions.checkNotNull(type);

        B target = createBundle(type);
        for (K key : target.getPossibleKeys()) {
            JsonElement member = object.get(key.getId());
            if (member != null) {
                try {
                    target.set(key, deserializeValue(key.getType(), member));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return target;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private Object deserializeValue(@Nonnull ValueType type, @Nonnull JsonElement element)
            throws IOException, SAXException, ParseException {
        Object simple = deserializeSimpleValue(type, element);

        if (simple != null) { return simple; }

        if (Bundle.class.isAssignableFrom(type.getType())) {
            Preconditions.checkArgument(element.isJsonObject());
            return deserializeBundle(element.getAsJsonObject(), (Class<? extends Bundle>) type.getType());
        }

        throw new UnsupportedOperationException(type + " is not supported for serialization.");
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private Object deserializeSimpleValue(@Nonnull ValueType type, @Nonnull JsonElement element)
            throws IOException, SAXException, ParseException {
        if (type.isContainer()) {
            assert type.getElementType() != null;
            if (type.getType() == Optional.class) {
                return element.isJsonNull() ?
                        Optional.missing(type.getElementType().getType()) :
                        Optional.existing(deserializeValue(type.getElementType(), element));
            } else if (Collection.class.isAssignableFrom(type.getType())) {
                Preconditions.checkArgument(element.isJsonArray());
                Collection<Object> d = type.getType() == List.class ? Lists.newArrayList() : Sets.newHashSet();
                for (JsonElement ele : element.getAsJsonArray()) {
                    d.add(deserializeValue(type.getElementType(), ele));
                }
                return d;
            }
        } else if (!Bundle.class.isAssignableFrom(type.getType())) {
            Preconditions.checkArgument(element.isJsonPrimitive(), element);
            if (type == ValueType.BOOLEAN) {
                Preconditions.checkArgument(element.getAsJsonPrimitive().isBoolean());
                return element.getAsBoolean();
            }
            if (type == ValueType.NUMBER) {
                Preconditions.checkArgument(element.getAsJsonPrimitive().isNumber());
                return element.getAsNumber();
            }
            if (type == ValueType.URL) {
                Preconditions.checkArgument(element.getAsJsonPrimitive().isString());
                try {
                    return new URL(element.getAsString());
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException(e);
                }
            }
            if (type == ValueType.STRING) {
                Preconditions.checkArgument(element.getAsJsonPrimitive().isString());
                return element.getAsString();
            }
            if (type == ValueType.FORMATTED_STRING) {
                Preconditions.checkArgument(element.getAsJsonPrimitive().isString());
                return FormattedStringParser.parseHtml(element.getAsJsonPrimitive().getAsString());
            }
            if (type == ValueType.DATE) {
                Preconditions.checkArgument(element.getAsJsonPrimitive().isString());
                return createIsoDateFormat().parse(element.getAsJsonPrimitive().getAsString());
            }
            if (Identifiable.class.isAssignableFrom(type.getType())) {
                Preconditions.checkArgument(element.getAsJsonPrimitive().isString());
                return findIdentifiable((Class<? extends Identifiable>) type.getType(), element.getAsString());
            }
        }

        return null;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private <B extends Bundle<?, ?>> B createBundle(@Nonnull Class<B> type) {
        if (type == Story.class) { return (B) Story.createMutable(); }
        if (type == Chapter.class) { return (B) Chapter.createMutable(); }
        if (type == User.class) { return (B) User.createMutable(); }
        if (type == SearchParameters.class) { return (B) SearchParameters.createMutable(); }
        if (type == SearchResult.class) { return (B) SearchResult.createMutable(); }
        throw new UnsupportedOperationException("Unsupported bundle type: " + type.getSimpleName());
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private <I extends Identifiable> I findIdentifiable(@Nonnull Class<I> type, @Nonnull String id) {
        if (type == Category.class) { return (I) Category.forId(id); }
        if (type == ContentRating.class) { return (I) ContentRating.forId(id); }
        if (type == FimCharacter.class) { return (I) FimCharacter.DefaultCharacter.forId(id); }
        if (type == Timeframe.class) { return (I) Timeframe.DefaultTimeframe.forId(id); }
        if (type == Order.class) { return (I) Order.forId(id); }
        if (type == Rating.class) { return (I) Rating.forId(id); }
        if (type == StoryStatus.class) { return (I) StoryStatus.forId(id); }
        throw new UnsupportedOperationException(
                "Unsupported identifiable type: " + type.getSimpleName() + " [value=" + id + "]");
    }

    static DateFormat createIsoDateFormat() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }
}
