package at.yawk.fimfiction.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Content rating (PEGI-like) the author has given for a story.
 *
 * @author Jonas Konrad (yawkat)
 */
public enum ContentRating implements Identifiable {
    /**
     * Everyone (0+).
     */
    EVERYONE("everyone"),
    /**
     * Teen (13+).
     */
    TEEN("teen"),
    /**
     * Mature (18+).
     */
    MATURE("mature");

    static { IdentifiableMapper.addMapping(ContentRating.class, values()); }

    @Nullable
    public static ContentRating forId(@Nonnull String id) {
        return IdentifiableMapper.findIdentifiable(ContentRating.class, id);
    }

    private final String id;

    ContentRating(String id) {
        this.id = id;
    }

    @Nonnull
    @Override
    public String getId() {
        return id;
    }
}
