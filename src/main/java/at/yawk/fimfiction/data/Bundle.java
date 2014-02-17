package at.yawk.fimfiction.data;

import at.yawk.fimfiction.core.MissingKeyException;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Abstract class for a data holder that stores a K-Object map and can be either mutable or immutable.
 * <p/>
 * The first type parameter should equal this class and is used for operations such as cloning.
 * <p/>
 * The second type parameter is the Key class used to access values from this Bundle.
 *
 * @author Jonas Konrad (yawkat)
 */
public abstract class Bundle<B extends Bundle, K extends Key> {
    Bundle() {}

    /**
     * The given String will be converted into a key with the same Key#getId.
     *
     * @param id a non-null String.
     * @return a non-null value associated with the key with the given ID.
     * @throws NullPointerException     if the parameter is null
     * @throws IllegalArgumentException if no key with the given id exists.
     * @throws MissingKeyException      if no value is associated to this key.
     * @see #get(Key)
     */
    @Nonnull
    public final <T> T get(@Nonnull String id) throws MissingKeyException, IllegalArgumentException {
        return get(key(id));
    }

    /**
     * @return the non-null value associated with the given key.
     * @throws NullPointerException if the parameter is null.
     * @throws MissingKeyException  if no such value is set in this bundle.
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public final <T> T get(@Nonnull K key) throws MissingKeyException {
        Preconditions.checkNotNull(key);

        return (T) ensureNotNull(key, _get(key));
    }

    /**
     * Assign the given Object value to the key associated with the given ID.
     *
     * @return this object.
     * @throws NullPointerException     if either argument is null
     * @throws IllegalArgumentException if no key with the given id exists or the key does not accept the given value.
     * @see #set(Key, Object)
     */
    @Nonnull
    public final B set(@Nonnull String id, @Nonnull Object value) {
        return set(key(id), value);
    }

    /**
     * Assign the given Object value to the given key.
     *
     * @return this object.
     * @throws NullPointerException     if either argument is null.
     * @throws IllegalArgumentException if the given key does not accept the given value.
     */
    @Nonnull
    public final B set(@Nonnull K key, @Nonnull Object value) {
        Preconditions.checkNotNull(value);
        _set(key, validate(key, value));
        return self();
    }

    /**
     * Copies all properties from the given Bundle of the same type as this object into this Bundle. Keys in this
     * Bundle that exist in the given Bundle as well will be overwritten.
     *
     * @return this object.
     * @throws NullPointerException     if the given argument is null.
     * @throws IllegalArgumentException if the given argument is of a different type than this object.
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public final B set(@Nonnull B other) {
        Preconditions.checkNotNull(other);
        Preconditions.checkArgument(other.getClass() == getClass());
        for (K key : _keySet()) {
            Object value = other._get(key);
            if (value != null) { _set(key, value); }
        }
        return self();
    }

    /**
     * @return true if this Bundle contains the key associated with the given ID, false otherwise.
     * @throws NullPointerException     if the parameter is null.
     * @throws IllegalArgumentException if there is no key with the given ID.
     * @see #has(Key)
     */
    public final boolean has(@Nonnull String id) {
        return _get(key(id)) != null;
    }

    /**
     * @return true if this Bundle contains a value associated with the given Key, false otherwise.
     * @throws NullPointerException if the given key is null.
     */
    public final boolean has(@Nonnull K key) {
        Preconditions.checkNotNull(key);
        return _get(key) != null;
    }

    /**
     * @return an immutable set of all possible keys that can be set in this Bundle.
     */
    @Nonnull
    public final Set<K> getPossibleKeys() {
        return _keySet();
    }

    /**
     * @return an immutable Set of all keys currently set in this bundle. This Set will update according to changes
     *         of this object.
     */
    @Nonnull
    public final Set<K> getSetKeys() {
        return Sets.filter(getPossibleKeys(), new Predicate<K>() {
            @Override
            public boolean apply(@Nullable K input) {
                return input != null && has(input);
            }
        });
    }

    /**
     * Removes any value set to the key associated with the given ID.
     *
     * @return this object.
     * @throws NullPointerException     if the parameter is null.
     * @throws IllegalArgumentException if no key with the given ID exists.
     * @see #unset(Key)
     */
    @Nonnull
    public final B unset(@Nonnull String id) {
        return unset(key(id));
    }

    /**
     * Removes any value associated to this key from this Bundle.
     *
     * @return this object.
     * @throws NullPointerException if the parameter is null.
     */
    @Nonnull
    public final B unset(@Nonnull K key) {
        Preconditions.checkNotNull(key);
        _set(key, null);
        return self();
    }

    /**
     * Convenience method equivalent to #get(Key) that returns an integer.
     */
    public final int getInt(@Nonnull K key) { return (this.<Number>get(key)).intValue(); }

    /**
     * Convenience method that returns either the value associated to the given key or the default value if no such
     * value exists.
     */
    public final int getInt(@Nonnull K key, int defaultValue) { return has(key) ? getInt(key) : defaultValue; }

    /**
     * Convenience method equivalent to #get(Key) that returns an boolean.
     */
    public final boolean getBoolean(@Nonnull K key) { return get(key); }

    /**
     * Convenience method that returns either the value associated to the given key or the default value if no such
     * value exists.
     */
    public final boolean getBoolean(@Nonnull K key, boolean defaultValue) {
        return has(key) ? getBoolean(key) : defaultValue;
    }

    /**
     * Convenience method equivalent to #get(Key) that returns an String.
     */
    @Nonnull
    public final String getString(@Nonnull K key) { return (this.<CharSequence>get(key)).toString(); }

    /**
     * Convenience method that returns either the value associated to the given key or the default value if no such
     * value exists.
     */
    @Nonnull
    public final String getString(@Nonnull K key, @Nonnull String defaultValue) {
        return has(key) ? getString(key) : defaultValue;
    }

    /**
     * @return A mutable clone of this Bundle. This is ensured to be a new Object.
     */
    @Nonnull
    public final B mutableCopy() {
        return _copy(false);
    }

    /**
     * @return An immutable clone of this Bundle. Depending on implementation, this might be the same object if this
     *         object is already immutable.
     */
    @Nonnull
    public final B immutableCopy() {
        return _immutable() ? self() : _copy(true);
    }

    /**
     * Method used to ensure a Bundle is mutable and thus suitable for following #set and #unset operations.
     *
     * @return A version of this object that is sure to be mutable. Depending on implementation,
     *         this might be the same object if this object is already mutable.
     */
    @Nonnull
    public final B mutableVersion() {
        return _immutable() ? _copy(false) : self();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) { return false; }

        for (K key : getPossibleKeys()) {
            Object v1 = _get(key);
            Object v2 = ((Bundle<?, K>) o)._get(key);
            if (v1 == null ? v2 != null : !v1.equals(v2)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public final int hashCode() {
        int h = 0;
        int o = 0;
        for (K key : getPossibleKeys()) {
            Object v = _get(key);
            h ^= v == null ? 0 : v.hashCode();
            o = (o + 7) % 25;
        }
        return h;
    }

    @Override
    public final String toString() {
        StringBuilder toString = new StringBuilder(getClass().getSimpleName());
        toString.append('{');
        for (K key : getSetKeys()) {
            toString.append(key.getId());
            toString.append("=\"");
            toString.append(_get(key));
            toString.append("\", ");
        }
        toString.setLength(toString.length() - 2);
        toString.append('}');
        return toString.toString();
    }

    // private

    @Nonnull
    private static Object ensureNotNull(@Nonnull Key key, @Nullable Object o) throws MissingKeyException {
        if (o == null) { throw new MissingKeyException(key); }
        return o;
    }

    @Nonnull
    private K key(@Nonnull String id) throws IllegalArgumentException {
        Preconditions.checkNotNull(id);

        K key = _findKey(id);
        if (key == null) { throw new IllegalArgumentException(id); }
        return key;
    }

    @Nonnull
    private Object validate(@Nonnull K key, @Nonnull Object object) throws IllegalArgumentException {
        _validate(key, object);

        if (object instanceof Set) { return ImmutableSet.copyOf((Set<?>) object); }
        if (object instanceof List) { return ImmutableList.copyOf((List<?>) object); }
        return object;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private B self() {
        return (B) this;
    }

    // Override

    protected abstract void _validate(@Nonnull K key, @Nonnull Object object) throws IllegalArgumentException;

    @Nullable
    protected abstract K _findKey(@Nonnull String id);

    @Nonnull
    protected abstract B _copy(boolean immutable);

    @Nullable
    protected abstract Object _get(@Nonnull K key);

    protected abstract void _set(@Nonnull K key, @Nullable Object value);

    @Nonnull
    protected abstract Set<K> _keySet();

    protected boolean _immutable() { return false; }
}
