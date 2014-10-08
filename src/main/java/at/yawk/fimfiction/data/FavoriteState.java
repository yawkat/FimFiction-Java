package at.yawk.fimfiction.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Favorite state for a story (unique for each user).
 *
 * @author Jonas Konrad (yawkat)
 * @deprecated Replaced by shelf API.
 */
@Deprecated
public enum FavoriteState implements Identifiable {
    /**
     * The user has not favorited this story.
     */
    NOT_FAVORITED("not_favorited"),
    /**
     * This story is favorited.
     */
    FAVORITED("favorited"),
    /**
     * This story is favorited and the user is receiving e-mail notifications whenever it's updated.
     */
    FAVORITED_WITH_EMAIL("favorited_email");

    static { IdentifiableMapper.addMapping(FavoriteState.class, values()); }

    @Nullable
    public static FavoriteState forId(@Nonnull String id) {
        return IdentifiableMapper.findIdentifiable(FavoriteState.class, id);
    }

    private final String id;

    FavoriteState(String id) {
        this.id = id;
    }

    @Nonnull
    @Override
    public String getId() {
        return id;
    }

    public boolean isFavorited() {
        return this != NOT_FAVORITED;
    }
}
