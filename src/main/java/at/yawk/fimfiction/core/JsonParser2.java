package at.yawk.fimfiction.core;

import at.yawk.fimfiction.data.*;
import at.yawk.fimfiction.net.NetUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.xml.sax.SAXException;

/**
 * @author Jonas Konrad (yawkat)
 */
class JsonParser2<B extends Bundle<B, K>, K extends Key> {
    private static final Map<String, Story.StoryKey> storyKeys;
    private static final Map<String, Chapter.ChapterKey> chapterKeys;
    private static final Map<String, User.UserKey> userKeys;

    static {
        Map<String, Story.StoryKey> k = Maps.newHashMap();
        k.put("id", Story.StoryKey.ID);
        k.put("title", Story.StoryKey.TITLE);
        k.put("url", Story.StoryKey.URL);
        k.put("description", Story.StoryKey.DESCRIPTION);
        k.put("short_description", Story.StoryKey.DESCRIPTION_SHORT);
        k.put("views", Story.StoryKey.VIEW_COUNT_MAXIMUM_CHAPTER);
        k.put("total_views", Story.StoryKey.VIEW_COUNT_TOTAL);
        k.put("words", Story.StoryKey.WORD_COUNT);
        k.put("comments", Story.StoryKey.COMMENT_COUNT);
        k.put("date_modified", Story.StoryKey.DATE_UPDATED);
        k.put("full_image", Story.StoryKey.URL_COVER);
        k.put("image", Story.StoryKey.URL_THUMBNAIL);
        k.put("status", Story.StoryKey.STATUS);
        k.put("content_rating", Story.StoryKey.CONTENT_RATING);
        k.put("content_rating_text", null);
        k.put("likes", Story.StoryKey.LIKE_COUNT);
        k.put("dislikes", Story.StoryKey.DISLIKE_COUNT);
        k.put("chapter_count", Story.StoryKey.CHAPTER_COUNT);
        k.put("categories", Story.StoryKey.CATEGORIES);
        k.put("author", Story.StoryKey.AUTHOR);
        k.put("chapters", Story.StoryKey.CHAPTERS);
        storyKeys = k;
    }

    static {
        Map<String, Chapter.ChapterKey> k = Maps.newHashMap();
        k.put("id", Chapter.ChapterKey.ID);
        k.put("title", Chapter.ChapterKey.TITLE);
        k.put("link", Chapter.ChapterKey.URL);
        k.put("words", Chapter.ChapterKey.WORD_COUNT);
        k.put("views", Chapter.ChapterKey.VIEW_COUNT);
        k.put("date_modified", Chapter.ChapterKey.DATE_MODIFIED);
        k.put("content", Chapter.ChapterKey.CONTENT);
        chapterKeys = k;
    }

    static {
        Map<String, User.UserKey> k = Maps.newHashMap();
        k.put("id", User.UserKey.ID);
        k.put("name", User.UserKey.NAME);
        k.put("num_followers", User.UserKey.FOLLOWER_COUNT);
        k.put("bio", User.UserKey.BIOGRAPHY);
        k.put("date_joined", User.UserKey.DATE_JOINED);
        k.put("avatar", User.UserKey.URL_PROFILE_IMAGE);
        userKeys = k;
    }

    private final B bundle;
    private final JsonReader input;
    private final Map<String, K> baseKeys;

    boolean skipStatus = false;

    static JsonParser2<Story, Story.StoryKey> story(JsonReader input) {
        return new JsonParser2<Story, Story.StoryKey>(Story.createMutable(), input, storyKeys);
    }

    static JsonParser2<Chapter, Chapter.ChapterKey> chapter(JsonReader input) {
        return new JsonParser2<Chapter, Chapter.ChapterKey>(Chapter.createMutable(), input, chapterKeys);
    }

    static JsonParser2<User, User.UserKey> user(JsonReader input) {
        return new JsonParser2<User, User.UserKey>(User.createMutable(), input, userKeys);
    }

    private JsonParser2(B bundle, JsonReader input, Map<String, K> baseKeys) {
        this.bundle = bundle;
        this.input = input;
        this.baseKeys = baseKeys;
    }

    public B parse() throws IOException, SAXException {
        input.beginObject();
        while (true) {
            if (!continueParsing()) { break; }
        }
        return bundle;
    }

    private boolean continueParsing() throws IOException, SAXException {
        if (input.peek() == JsonToken.END_OBJECT) {
            input.endObject();
            return false;
        }
        if (input.peek() == JsonToken.END_DOCUMENT) { return false; }
        String name = input.nextName();
        if ("story".equals(name)) {
            input.beginObject();
            return true;
        }
        K key = baseKeys.get(name);
        if (key == null) {
            assert baseKeys.containsKey(name) : name;
            while (input.peek() != JsonToken.NAME) { input.skipValue(); }
            return true;
        }
        if (skipStatus && key == Story.StoryKey.STATUS) {
            input.nextString();
            return true;
        }
        Object value;
        if (key == User.UserKey.URL_PROFILE_IMAGE && input.peek() == JsonToken.BEGIN_OBJECT) {
            input.beginObject();
            while (input.peek() != JsonToken.STRING) { input.skipValue(); }
            String url = input.nextString();
            value = url.endsWith("none_64.png") ?
                    Optional.missing(URL.class) :
                    Optional.existing(parseSpecial(ValueType.URL, url));
            while (input.peek() != JsonToken.END_OBJECT) { input.skipValue(); }
            input.endObject();
        } else {
            value = parseValue(key.getType());
        }
        bundle.set(key, value);
        return true;
    }

    private Object parseValue(ValueType type) throws IOException, SAXException {
        switch (type) {
        case NUMBER:
            try {
                return input.nextLong();
            } catch (NumberFormatException e) {
                return input.nextDouble();
            }
        case URL:
        case STORY_STATUS:
        case STRING:
        case CATEGORY:
        case FORMATTED_STRING:
            return parseSpecial(type, input.nextString());
        case DATE:
            return new Date(input.nextLong() * 1000L);
        case BOOLEAN:
            return input.nextBoolean();
        case USER:
            return user(input).parse();
        case CONTENT_RATING:
            int id = input.nextInt();
            return id == 0 ? ContentRating.EVERYONE : id == 1 ? ContentRating.TEEN : ContentRating.MATURE;
        case CHAPTER:
            return chapter(input).parse();
        case STORY:
            return story(input).parse();
        case OPTIONAL_URL:
        case OPTIONAL_USER:
        case OPTIONAL_STRING:
            ValueType elementType = type.getElementType();
            assert elementType != null;
            return input.peek() == JsonToken.NULL ?
                    Optional.missing(elementType.getType()) :
                    Optional.existing(parseValue(elementType), (Class) elementType.getType());
        case LIST_CHAPTER:
        case LIST_STORY:
        case SET_CHARACTER:
        case SET_CATEGORY:
            Collection<Object> c = type.getType() == List.class ? Lists.newArrayList() : Sets.newHashSet();
            if (input.peek() == JsonToken.BEGIN_ARRAY) {
                input.beginArray();
                while (input.peek() != JsonToken.END_ARRAY) {
                    c.add(parseValue(type.getElementType()));
                }
                input.endArray();
            } else {
                input.beginObject();
                while (input.peek() != JsonToken.END_OBJECT) {
                    String name = input.nextName();
                    boolean select = input.nextBoolean();
                    if (select) {
                        c.add(parseSpecial(type.getElementType(), name));
                    }
                }
                input.endObject();
            }
            return c;
        }
        throw new UnsupportedOperationException(type.toString());
    }

    private Object parseSpecial(ValueType type, String val) throws IOException, SAXException {
        switch (type) {
        case STRING:
            return val;
        case STORY_STATUS:
            if ("Complete".equals(val)) { return StoryStatus.COMPLETED; }
            if ("Incomplete".equals(val)) { return StoryStatus.INCOMPLETE; }
            if ("On Hiatus".equals(val)) { return StoryStatus.ON_HIATUS; }
            if ("Cancelled".equals(val)) { return StoryStatus.CANCELLED; }
            break;
        case URL:
            return NetUtil.createUrlNonNull(val.charAt(0) == 'h' ? val : "http:" + val);
        case CATEGORY:
            if ("Romance".equals(val) || "romance".equals(val)) { return Category.ROMANCE; }
            if ("Tragedy".equals(val) || "tragedy".equals(val)) { return Category.TRAGEDY; }
            if ("Sad".equals(val) || "sad".equals(val)) { return Category.SAD; }
            if ("Dark".equals(val) || "dark".equals(val)) { return Category.DARK; }
            if ("Comedy".equals(val) || "comedy".equals(val)) { return Category.COMEDY; }
            if ("Random".equals(val) || "random".equals(val)) { return Category.RANDOM; }
            if ("Crossover".equals(val) || "crossover".equals(val)) { return Category.CROSSOVER; }
            if ("Adventure".equals(val) || "adventure".equals(val)) { return Category.ADVENTURE; }
            if ("Slice of Life".equals(val) || "slice_of_life".equals(val)) { return Category.SLICE_OF_LIFE; }
            if ("Alternate Universe".equals(val) || "alternate_universe".equals(val)) {
                return Category.ALTERNATE_UNIVERSE;
            }
            if ("Human".equals(val) || "human".equals(val)) { return Category.HUMAN; }
            if ("Anthro".equals(val) || "anthro".equals(val)) { return Category.ANTHRO; }
            break;
        case FORMATTED_STRING:
            if (val.startsWith("<p>")) {
                return FormattedStringParser.parseHtml(val);
            } else {
                return FormattedStringParser.parseBb(val);
            }
        }
        throw new UnsupportedOperationException(type.toString());
    }

}
