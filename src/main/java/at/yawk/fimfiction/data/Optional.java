package at.yawk.fimfiction.data;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Wrapper class for optional objects. Because Bundles do not accept null values, this class is used in places where
 * it a value is known to be non-existing. One example is a story cover image: If the value is simply unset,
 * the cover image is unknown, if it is a missing optional, the cover image is known to not exist.
 *
 * @author Jonas Konrad (yawkat)
 */
public final class Optional<T> {
    @Nonnull private final Class<? super T> type;
    @Nullable private final T value;

    private Optional(@Nonnull Class<? super T> type, @Nullable T value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Create a new, existing Optional with a non-null value. The Optional's type will be the class of the given value.
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> existing(@Nonnull T value) {
        Preconditions.checkNotNull(value);
        return new Optional<T>((Class<T>) value.getClass(), value);
    }

    /**
     * Create a new, existing Optional with a non-null value and a given type. The value must be of the given type or
     * of one of its subclasses.
     */
    @Nonnull
    public static <T> Optional<T> existing(@Nonnull T value, @Nonnull Class<? super T> type) {
        Preconditions.checkNotNull(value);
        Preconditions.checkNotNull(type);
        Preconditions.checkArgument(type.isInstance(value));
        return new Optional<T>(type, value);
    }

    /**
     * A missing (null) optional.
     */
    @Nonnull
    public static <T> Optional<T> missing(@Nonnull Class<T> type) {
        Preconditions.checkNotNull(type);
        return new Optional<T>(type, null);
    }

    /**
     * Returns the type of this optional. The value is an instance of this type (if it is not null).
     */
    @Nonnull
    public Class<? super T> getType() {
        return type;
    }

    /**
     * Returns true if there is a value in this Optional, false otherwise.
     */
    public boolean exists() {
        return value != null;
    }

    /**
     * Returns the value of this optional.
     *
     * @throws IllegalStateException if this optional is missing.
     */
    @Nonnull
    public T get() throws IllegalStateException {
        Preconditions.checkState(exists());
        assert value != null;
        return value;
    }

    /**
     * Returns either the non-null value of this Optional or null if this Optional is missing.
     */
    @Nullable
    public T getOrNull() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Optional &&
               ((Optional) o).getType() == getType() &&
               (exists() ? get().equals(((Optional) o).getOrNull()) : !((Optional) o).exists());
    }

    @Override
    public int hashCode() {
        return getType().hashCode() ^ (exists() ? get().hashCode() : 0);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<" + getType().getSimpleName() + ">{" + getOrNull() + "}";
    }
}
