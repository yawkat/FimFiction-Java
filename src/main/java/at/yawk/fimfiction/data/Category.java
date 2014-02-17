package at.yawk.fimfiction.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Jonas Konrad (yawkat)
 */
public enum Category implements Identifiable {
    /**
     * Romantic relationship (shipfic) or similar content.
     */
    ROMANCE("romance"),
    /**
     * Bad ending.
     */
    TRAGEDY("tragedy"),
    /**
     * Content written to sadden the reader.
     */
    SAD("sad"),
    /**
     * Generally grim environment.
     */
    DARK("dark"),
    /**
     * Comedic content that tries to amuse the reader.
     */
    COMEDY("comedy"),
    /**
     * General lack of common sense and natural thinking.
     */
    RANDOM("random"),
    /**
     * Storyline crosses events / characters with another universe (tv show,
     * game or similar).
     */
    CROSSOVER("crossover"),
    /**
     * Events that affect the universe of the story.
     */
    ADVENTURE("adventure"),
    /**
     * Every-day life.
     */
    SLICE_OF_LIFE("slice_of_life"),
    /**
     * Stories which contain elements which do not agree with the normal MLP
     * universe.
     */
    ALTERNATE_UNIVERSE("alternate_universe"),
    /**
     * Content with humans of any kind.
     */
    HUMAN("human"),
    /**
     * Characters are not ponies but have a human or human-like body.
     */
    ANTHRO("anthro");

    static { IdentifiableMapper.addMapping(Category.class, values()); }

    @Nullable
    public static Category forId(@Nonnull String id) {
        return IdentifiableMapper.findIdentifiable(Category.class, id);
    }

    private final String id;

    Category(String id) {
        this.id = id;
    }


    @Nonnull
    @Override
    public String getId() {
        return id;
    }
}
