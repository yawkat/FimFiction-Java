package at.yawk.fimfiction.data;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Collection of search parameters that Fimfiction can compute.
 *
 * @author Jonas Konrad (yawkat)
 */
public class SearchParameters extends EnumKeyBundle<SearchParameters, SearchParameters.SearchParameter> {
    static { addMapping(SearchParameters.class, SearchParameter.class); }

    SearchParameters() {}

    @Nonnull
    public static SearchParameters createMutable() { return mutable(SearchParameters.class); }

    @Nonnull
    public static SearchParameters createMutableFromData(@Nonnull Map<SearchParameter, Object> data) {
        return mutable(SearchParameters.class, data);
    }

    @Nonnull
    public static SearchParameters createMutableFromStringData(@Nonnull Map<String, Object> data) {
        return mutableS(SearchParameters.class, data);
    }

    @Nonnull
    public static SearchParameters createImmutable() { return immutable(SearchParameters.class); }

    @Nonnull
    public static SearchParameters createImmutableFromData(@Nonnull Map<SearchParameter, Object> data) {
        return immutable(SearchParameters.class, data);
    }

    @Nonnull
    public static SearchParameters createImmutableFromStringData(@Nonnull Map<String, Object> data) {
        return immutableS(SearchParameters.class, data);
    }

    public static enum SearchParameter implements Key {
        NAME("name", ValueType.STRING),
        ORDER("order", ValueType.ORDER),
        CATEGORIES_INCLUDED("categories_included", ValueType.SET_CATEGORY),
        CATEGORIES_EXCLUDED("categories_excluded", ValueType.SET_CATEGORY),
        CHARACTERS_INCLUDED("characters_included", ValueType.SET_CHARACTER),
        CHARACTERS_EXCLUDED("characters_excluded", ValueType.SET_CHARACTER),
        CONTENT_RATING("content_rating", ValueType.CONTENT_RATING),
        SEX("sex", ValueType.BOOLEAN),
        GORE("gore", ValueType.BOOLEAN),
        COMPLETED("completed", ValueType.BOOLEAN),
        WORD_COUNT_MAXIMUM("word_count_maximum", ValueType.NUMBER),
        WORD_COUNT_MINIMUM("word_count_minimum", ValueType.NUMBER),
        UNREAD("unread", ValueType.BOOLEAN),
        /**
         * @deprecated Not functional anymore, use shelf API.
         */
        @Deprecated
        FAVORITED("favorited", ValueType.BOOLEAN),
        /**
         * @deprecated Not functional anymore, use shelf API.
         */
        @Deprecated
        READ_LATER("read_later", ValueType.BOOLEAN),
        USER("user", ValueType.USER),
        PUBLISH_TIMEFRAME("publish_timeframe", ValueType.TIMEFRAME),
        SHELF("shelf", ValueType.SHELF);

        @Nullable
        public static SearchParameter findKey(@Nonnull String id) {
            return SearchParameters.findKey(SearchParameters.class, id);
        }

        private final String id;
        private final ValueType type;

        private SearchParameter(String id, ValueType type) {
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
