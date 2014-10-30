package at.yawk.fimfiction.data;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A Bundle implementation representing a Story.
 *
 * @author Jonas Konrad (yawkat)
 */
public class Story extends EnumKeyBundle<Story, Story.StoryKey> {
    static { addMapping(Story.class, StoryKey.class); }

    Story() {}

    /**
     * @return A new empty, mutable Story object.
     */
    @Nonnull
    public static Story createMutable() { return mutable(Story.class); }

    /**
     * @return A new mutable Story object filled with the given data.
     */
    @Nonnull
    public static Story createMutableFromData(@Nonnull Map<StoryKey, Object> data) {
        return mutable(Story.class, data);
    }

    /**
     * @return A new mutable Story object filled with the given data.
     */
    @Nonnull
    public static Story createMutableFromStringData(@Nonnull Map<String, Object> data) {
        return mutableS(Story.class, data);
    }

    /**
     * @return An empty, immutable Story object.
     */
    @Nonnull
    public static Story createImmutable() { return immutable(Story.class); }

    /**
     * @return An immutable Story object filled with the given data.
     */
    @Nonnull
    public static Story createImmutableFromData(@Nonnull Map<StoryKey, Object> data) {
        return immutable(Story.class, data);
    }

    /**
     * @return An immutable Story object filled with the given data.
     */
    @Nonnull
    public static Story createImmutableFromStringData(@Nonnull Map<String, Object> data) {
        return immutableS(Story.class, data);
    }

    public static enum StoryKey implements Key {
        /**
         * Numeric ID of this story used for internal referencing.
         */
        ID("id", ValueType.NUMBER),
        /**
         * The title of this story.
         */
        TITLE("title", ValueType.STRING),
        /**
         * The desktop URL of this story.
         */
        URL("url", ValueType.URL),
        /**
         * The full formatted description of this Story.
         */
        DESCRIPTION("description", ValueType.FORMATTED_STRING),
        /**
         * The short description String of this Story.
         */
        DESCRIPTION_SHORT("description_short", ValueType.STRING),
        /**
         * The Date representing the time when this Story was first posted.
         */
        DATE_FIRST_POSTED("date_first_posted", ValueType.DATE),
        /**
         * The Date representing the time when this Story was last updated.
         */
        DATE_UPDATED("date_updated", ValueType.DATE),
        /**
         * URL to the thumbnail image (scaled down #URL_COVER) of this Story.
         */
        URL_THUMBNAIL("url_thumbnail", ValueType.OPTIONAL_URL),
        /**
         * URL to the cover image of this Story.
         */
        URL_COVER("url_cover", ValueType.OPTIONAL_URL),
        /**
         * View count of the most viewed chapter of this Story.
         */
        VIEW_COUNT_MAXIMUM_CHAPTER("view_count_maximum_chapter", ValueType.NUMBER),
        /**
         * Total view count of this Story across all chapters.
         */
        VIEW_COUNT_TOTAL("view_count_total", ValueType.NUMBER),
        /**
         * Amount of words in this Story.
         */
        WORD_COUNT("word_count", ValueType.NUMBER),
        /**
         * Amount of chapters in this Story; should be equivalent to CHAPTERS#size().
         */
        CHAPTER_COUNT("chapter_count", ValueType.NUMBER),
        /**
         * Amount of comments that were posted on this Story.
         */
        COMMENT_COUNT("comment_count", ValueType.NUMBER),
        /**
         * Amount of likes this Story has received.
         */
        LIKE_COUNT("like_count", ValueType.NUMBER),
        /**
         * Amount of dislikes this Story has received.
         */
        DISLIKE_COUNT("dislike_count", ValueType.NUMBER),
        /**
         * Author (User) object of this Story.
         */
        AUTHOR("author", ValueType.USER),
        /**
         * Current completion status specified by the author of this Story.
         */
        STATUS("status", ValueType.STORY_STATUS),
        /**
         * Content rating specified by the author of this Story.
         */
        CONTENT_RATING("content_rating", ValueType.CONTENT_RATING),
        /**
         * Set of categories assigned to this Story.
         */
        CATEGORIES("categories", ValueType.SET_CATEGORY),
        /**
         * Set of characters assigned to this Story.
         */
        CHARACTERS("characters", ValueType.SET_CHARACTER),
        /**
         * List of chapters of this Story.
         */
        CHAPTERS("chapters", ValueType.LIST_CHAPTER),
        /**
         * Sex content rating flag specified by the author of this Story.
         */
        SEX("sex", ValueType.BOOLEAN),
        /**
         * Gore content rating flag specified by the author of this Story.
         */
        GORE("gore", ValueType.BOOLEAN),

        /**
         * Current favorite state the user has assigned to this Story.
         *
         * @deprecated Replaced by shelf system.
         */
        @Deprecated
        FAVORITE_STATE("favorite_state", ValueType.FAVORITE_STATE),
        /**
         * Read later flag for this Story for this User.
         *
         * @deprecated Replaced by shelf system.
         */
        @Deprecated
        READ_LATER_STATE("read_later_state", ValueType.BOOLEAN),
        /**
         * Rating this User has given this Story.
         */
        RATING("rating", ValueType.RATING),
        /**
         * Rating token String used for rating this story.
         *
         * @deprecated Unused.
         */
        @Deprecated
        RATING_TOKEN("rating_token", ValueType.STRING),
        /**
         * Shelves this story is part of.
         */
        SHELVES_ADDED("shelves_added", ValueType.SET_SHELF),
        /**
         * Shelves this story is not part of.
         */
        SHELVES_NOT_ADDED("shelves_not_added", ValueType.SET_SHELF);

        /**
         * @return The story key associated to the given ID or null if no such key exists.
         */
        @Nullable
        public static StoryKey findKey(@Nonnull String id) {
            return Story.findKey(Story.class, id);
        }

        private final String id;
        private final ValueType type;

        private StoryKey(String id, ValueType type) {
            this.id = id;
            this.type = type;
        }

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
