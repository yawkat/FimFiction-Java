package at.yawk.fimfiction.data;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A chapter of a Story.
 *
 * @author Jonas Konrad (yawkat)
 */
public class Chapter extends EnumKeyBundle<Chapter, Chapter.ChapterKey> {
    static { addMapping(Chapter.class, ChapterKey.class); }

    Chapter() {}

    @Nonnull
    public static Chapter createMutable() { return mutable(Chapter.class); }

    @Nonnull
    public static Chapter createMutableFromData(@Nonnull Map<ChapterKey, Object> data) {
        return mutable(Chapter.class, data);
    }

    @Nonnull
    public static Chapter createMutableFromStringData(@Nonnull Map<String, Object> data) {
        return mutableS(Chapter.class, data);
    }

    @Nonnull
    public static Chapter createImmutable() { return immutable(Chapter.class); }

    @Nonnull
    public static Chapter createImmutableFromData(@Nonnull Map<ChapterKey, Object> data) {
        return immutable(Chapter.class, data);
    }

    @Nonnull
    public static Chapter createImmutableFromStringData(@Nonnull Map<String, Object> data) {
        return immutableS(Chapter.class, data);
    }

    public static enum ChapterKey implements Key {
        /**
         * This chapter's unique numeric ID. This is not correlated to the story ID.
         */
        ID("id", ValueType.NUMBER),
        /**
         * The unformatted title string of this story.
         */
        TITLE("title", ValueType.STRING),
        /**
         * The full URL of this chapter.
         */
        URL("url", ValueType.URL),
        /**
         * Approximate amount of words in this chapter as calculated by fimfiction.
         */
        WORD_COUNT("word_count", ValueType.NUMBER),
        /**
         * Amount of views this chapter has received.
         */
        VIEW_COUNT("view_count", ValueType.NUMBER),
        /**
         * Date this chapter was last modified.
         */
        DATE_MODIFIED("date_modified", ValueType.DATE),
        /**
         * Formatted text content of this story.
         */
        CONTENT("content", ValueType.FORMATTED_STRING),
        /**
         * Unread status as a boolean (true means that this chapter is unread).
         */
        UNREAD("unread", ValueType.BOOLEAN);

        @Nullable
        public static ChapterKey findKey(@Nonnull String id) {
            return Chapter.findKey(Chapter.class, id);
        }

        ChapterKey(@Nonnull String id, @Nonnull ValueType type) {
            this.id = id;
            this.type = type;
        }

        @Nonnull private final String id;
        @Nonnull private final ValueType type;

        @Nonnull
        @Override
        public String getId() {
            return id;
        }

        @Nonnull
        @Override
        public ValueType getType() {
            return type;
        }
    }
}
