package at.yawk.fimfiction.data;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Jonas Konrad (yawkat)
 */
public final class User extends EnumKeyBundle<User, User.UserKey> {
    static { addMapping(User.class, UserKey.class); }

    User() {}

    @Nonnull
    public static User createMutable() { return mutable(User.class); }

    @Nonnull
    public static User createMutableFromData(@Nonnull Map<UserKey, Object> data) { return mutable(User.class, data); }

    @Nonnull
    public static User createMutableFromStringData(@Nonnull Map<String, Object> data) {
        return mutableS(User.class, data);
    }

    @Nonnull
    public static User createImmutable() { return immutable(User.class); }

    @Nonnull
    public static User createImmutableFromData(@Nonnull Map<UserKey, Object> data) {
        return immutable(User.class, data);
    }

    @Nonnull
    public static User createImmutableFromStringData(@Nonnull Map<String, Object> data) {
        return immutableS(User.class, data);
    }

    public static enum UserKey implements Key {
        ID("id", ValueType.NUMBER),
        NAME("name", ValueType.STRING),
        URL_PROFILE_IMAGE("url_profile_image", ValueType.OPTIONAL_URL),
        FOLLOWER_COUNT("follower_count", ValueType.NUMBER),
        BIOGRAPHY("biography", ValueType.FORMATTED_STRING),
        DATE_JOINED("date_joined", ValueType.DATE);

        @Nullable
        public static UserKey findKey(@Nonnull String id) {
            return User.findKey(User.class, id);
        }

        private final String id;
        private final ValueType type;

        UserKey(String id, ValueType type) {
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
