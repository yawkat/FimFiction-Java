package at.yawk.fimfiction.data;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author yawkat
 */
public class Shelf extends EnumKeyBundle<Shelf, Shelf.ShelfKey> {
    static { addMapping(Shelf.class, ShelfKey.class); }

    Shelf() {}

    @Nonnull
    public static Shelf createMutable() { return mutable(Shelf.class); }

    @Nonnull
    public static Shelf createMutableFromData(@Nonnull Map<ShelfKey, Object> data) {
        return mutable(Shelf.class, data);
    }

    @Nonnull
    public static Shelf createMutableFromStringData(@Nonnull Map<String, Object> data) {
        return mutableS(Shelf.class, data);
    }

    @Nonnull
    public static Shelf createImmutable() { return immutable(Shelf.class); }

    @Nonnull
    public static Shelf createImmutableFromData(@Nonnull Map<ShelfKey, Object> data) {
        return immutable(Shelf.class, data);
    }

    @Nonnull
    public static Shelf createImmutableFromStringData(@Nonnull Map<String, Object> data) {
        return immutableS(Shelf.class, data);
    }

    public static enum ShelfKey implements Key {
        ID("id", ValueType.NUMBER),
        NAME("name", ValueType.STRING),
        QUICK_ADD("quick_add", ValueType.BOOLEAN),
        ;

        @Nullable
        public static ShelfKey findKey(@Nonnull String id) {
            return Shelf.findKey(Shelf.class, id);
        }

        private final String id;
        private final ValueType type;

        ShelfKey(String id, ValueType type) {
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
