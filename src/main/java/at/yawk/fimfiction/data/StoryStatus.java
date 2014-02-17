package at.yawk.fimfiction.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Jonas Konrad (yawkat)
 */
public enum StoryStatus implements Identifiable {
    /**
     * In progress.
     */
    INCOMPLETE("incomplete"),
    /**
     * Cancelled and not to be continued.
     */
    CANCELLED("cancelled"),
    /**
     * Cancelled temporarily, might be continued at a later date.
     */
    ON_HIATUS("on_hiatus"),
    /**
     * Finished.
     */
    COMPLETED("completed");

    static { IdentifiableMapper.addMapping(StoryStatus.class, values()); }

    @Nullable
    public static StoryStatus forId(String id) {
        return IdentifiableMapper.findIdentifiable(StoryStatus.class, id);
    }

    private final String id;

    StoryStatus(String id) {
        this.id = id;
    }

    @Nonnull
    @Override
    public String getId() {
        return id;
    }
}
