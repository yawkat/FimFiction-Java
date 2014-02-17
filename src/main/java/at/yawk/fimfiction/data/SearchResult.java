package at.yawk.fimfiction.data;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Jonas Konrad (yawkat)
 */
public class SearchResult extends EnumKeyBundle<SearchResult, SearchResult.SearchResultKey> {
    static { addMapping(SearchResult.class, SearchResultKey.class); }

    SearchResult() {}

    @Nonnull
    public static SearchResult createMutable() { return mutable(SearchResult.class); }

    @Nonnull
    public static SearchResult createMutableFromData(@Nonnull Map<SearchResultKey, Object> data) {
        return mutable(SearchResult.class, data);
    }

    @Nonnull
    public static SearchResult createMutableFromStringData(@Nonnull Map<String, Object> data) {
        return mutableS(SearchResult.class, data);
    }

    @Nonnull
    public static SearchResult createImmutable() { return immutable(SearchResult.class); }

    @Nonnull
    public static SearchResult createImmutableFromData(@Nonnull Map<SearchResultKey, Object> data) {
        return immutable(SearchResult.class, data);
    }

    @Nonnull
    public static SearchResult createImmutableFromStringData(@Nonnull Map<String, Object> data) {
        return immutableS(SearchResult.class, data);
    }

    public static enum SearchResultKey implements Key {
        STORIES("stories", ValueType.LIST_STORY),
        LOGGED_IN_USER("logged_in_user", ValueType.OPTIONAL_USER),
        LOGOUT_NONCE("logout_nonce", ValueType.OPTIONAL_STRING);

        @Nullable
        public static SearchResultKey findKey(@Nonnull String id) {
            return SearchResult.findKey(SearchResult.class, id);
        }

        private final String id;
        private final ValueType type;

        SearchResultKey(String id, ValueType type) {
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
