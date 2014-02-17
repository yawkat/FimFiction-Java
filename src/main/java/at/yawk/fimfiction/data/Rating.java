package at.yawk.fimfiction.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Rating state for a Story and a User.
 *
 * @author Jonas Konrad (yawkat)
 */
public enum Rating implements Identifiable {
    /**
     * The user has liked the story.
     */
    LIKE("like"),
    /**
     * The user has disliked the story.
     */
    DISLIKE("dislike"),
    /**
     * The user has not marked the story as liked or disliked.
     */
    NONE("none");

    static { IdentifiableMapper.addMapping(Rating.class, values()); }

    @Nullable
    public static Rating forId(@Nonnull String id) {
        return IdentifiableMapper.findIdentifiable(Rating.class, id);
    }

    private final String id;

    Rating(String id) {
        this.id = id;
    }

    @Nonnull
    @Override
    public String getId() {
        return id;
    }
}
