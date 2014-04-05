package at.yawk.fimfiction.data;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Enum describing the different value types used in Bundles. This class can be changed frequently.
 *
 * @author Jonas Konrad (yawkat)
 */
public enum ValueType {
    /**
     * Any number. Instances must be of the Number type and be immutable but do not have to fulfil any other
     * contracts. Do not cast values but use methods like intValue instead.
     */
    NUMBER(Number.class),
    /**
     * A String instance.
     */
    STRING(String.class),
    /**
     * A FormattedString instance.
     */
    FORMATTED_STRING(FormattedString.class),
    /**
     * An URL instance.
     */
    URL(java.net.URL.class),
    /**
     * A Date instance.
     */
    DATE(Date.class),
    /**
     * A Boolean.
     */
    BOOLEAN(Boolean.class),
    /**
     * A User bundle.
     */
    USER(User.class),
    /**
     * A StoryStatus enum constant.
     */
    STORY_STATUS(StoryStatus.class),
    /**
     * A ContentRating enum constant.
     */
    CONTENT_RATING(ContentRating.class),
    /**
     * A FavoriteState enum constant.
     */
    FAVORITE_STATE(FavoriteState.class),
    /**
     * A Rating enum constant.
     */
    RATING(Rating.class),
    /**
     * A Order enum constant.
     */
    ORDER(Order.class),
    /**
     * A Chapter bundle.
     */
    CHAPTER(Chapter.class),
    /**
     * A Story bundle.
     */
    STORY(Story.class),
    /**
     * A FimCharacter (not necessarily a DefaultCharacter!).
     */
    CHARACTER(FimCharacter.class),
    /**
     * A Category enum constant.
     */
    CATEGORY(Category.class),
    /**
     * A Timeframe (not necessarily a DefaultTimeframe!).
     */
    TIMEFRAME(Timeframe.class),


    /**
     * An Optional instance with a User as the element.
     */
    OPTIONAL_USER(Optional.class, USER),
    /**
     * An Optional instance with a URL as the element.
     */
    OPTIONAL_URL(Optional.class, URL),
    /**
     * An Optional instance with a String as the element.
     */
    OPTIONAL_STRING(Optional.class, STRING),

    /**
     * A list of Chapter instances.
     */
    LIST_CHAPTER(List.class, CHAPTER),
    /**
     * A list of Story instances.
     */
    LIST_STORY(List.class, STORY),
    /**
     * A set of FimCharacter instances.
     */
    SET_CHARACTER(Set.class, CHARACTER),
    /**
     * A set of Category enum constants.
     */
    SET_CATEGORY(Set.class, CATEGORY);

    @Nonnull private final Class<?> type;
    @Nullable private final ValueType childType;

    ValueType(@Nonnull Class<?> type) {
        this(type, null);
    }

    ValueType(@Nonnull Class<?> type, @Nullable ValueType childType) {
        this.type = type;
        this.childType = childType;
    }

    /**
     * @return Class that any value Objects of this type should inherit.
     */
    @Nonnull
    public Class<?> getType() {
        return type;
    }

    /**
     * @return The element type of this container type or null if this is no container type.
     */
    @Nullable
    public ValueType getElementType() {
        return childType;
    }

    /**
     * @return true if this is a container type such as an Optional or a Collection, false otherwise.
     */
    public boolean isContainer() {
        return getElementType() != null;
    }

    /**
     * Attempts to validate the given non-null object to this ValueType.
     *
     * @return true if the given Object is a valid value to this type, false otherwise.
     * @throws NullPointerException if the parameter is null.
     */
    public boolean validate(@Nonnull Object o) {
        Preconditions.checkNotNull(o);

        if (!type.isInstance(o)) { return false; }

        if (childType != null) {
            assert getElementType() != null;
            if (o instanceof Iterable) { return getElementType().validateList((Iterable<?>) o); }
            if (o instanceof Optional) {
                return !((Optional) o).exists() || getElementType().validate(((Optional) o).get());
            }
            // this should never happen
            throw new IllegalStateException(this.toString());
        } else {
            return true;
        }
    }

    private boolean validateList(@Nonnull Iterable<?> iterable) {
        for (Object o : iterable) { if (!validate(o)) { return false; } }
        return true;
    }

    /**
     * Copies or reuses the given instance of this value type ensuring that it is immutable.
     */
    public Object immutableCopy(Object original) {
        Preconditions.checkState(getType().isInstance(original));

        switch (this) {
        case NUMBER:
        case STRING:
        case FORMATTED_STRING:
        case URL:
        case BOOLEAN:
        case CONTENT_RATING:
        case FAVORITE_STATE:
        case ORDER:
        case RATING:
        case CHARACTER:
        case CATEGORY:
        case TIMEFRAME:
            return original;
        case DATE:
            return ((Date) original).clone();
        case USER:
        case STORY_STATUS:
        case CHAPTER:
        case STORY:
            return ((Bundle) original).immutableCopy();
        case OPTIONAL_USER:
        case OPTIONAL_URL:
        case OPTIONAL_STRING:
            assert childType != null;
            Optional optional = (Optional) original;
            return optional.exists() ?
                    Optional.existing(childType.immutableCopy(optional.get()), (Class) childType.getType()) :
                    Optional.missing(childType.getType());
        case LIST_CHAPTER:
        case LIST_STORY:
        case SET_CHARACTER:
        case SET_CATEGORY:
            assert childType != null;
            Iterable<Object> v = Iterables.transform((Iterable<?>) original, new Function<Object, Object>() {
                @Nullable
                @Override
                public Object apply(@Nullable Object input) {
                    return input == null ? null : childType.immutableCopy(input);
                }
            });
            return Set.class.isAssignableFrom(getType()) ? ImmutableSet.copyOf(v) : ImmutableList.copyOf(v);
        }

        throw new AssertionError(name());
    }

    @Nonnull
    @Override
    public String toString() {
        return getType().getSimpleName() + (isContainer() ? "<" + getElementType() + ">" : "");
    }
}
