package at.yawk.fimfiction;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Set;

import lombok.Value;
import lombok.experimental.Builder;
import lombok.experimental.Wither;

/**
 * Immutable story. Note that some values may be missing or <code>null</code>
 * because not all request methods support all types of metadata. New fields
 * might be added in later api versions.
 * 
 * @author Yawkat
 */
@Value
@Builder
@Wither
public class Story {
    /**
     * Unique ID of the story. Can be used for caching.
     */
    int id;
    /**
     * URL under which this story can be found.
     */
    URL url;
    
    /**
     * Title of this story.
     */
    String title;
    /**
     * Long (normal) description of this story.
     */
    String description;
    /**
     * Brief description of this story, rarely used by the FimFiction website.
     */
    String descriptionShort;
    
    /**
     * Time of first posted chapter.
     * 
     * @see Date#Date(long)
     */
    long firstPostedDate;
    
    /**
     * Time of last update or addition of a chapter.
     * 
     * @see Date#Date(long)
     */
    long modificationDate;
    
    /**
     * {@link URL} of a smaller (thumbnail) version of {@link #imageUrl}. May be
     * <code>null</code> if unknown or none set.
     */
    URL thumbnailUrl;
    /**
     * Cover image of this story. May be <code>null</code> if unknown or none
     * set.
     */
    URL imageUrl;
    
    /**
     * Amount of views the {@link Chapter} with the most views has received.
     */
    int maximumChapterViewCount;
    /**
     * Total amount of views this {@link Story} has received.
     */
    int totalViewCount;
    /**
     * Total amount of words across all {@link Chapter}s.
     */
    int wordCount;
    /**
     * Amount of {@link Chapter}s (should equal
     * <code>{@link #chapters}.size()</code>, if exists).
     */
    int chapterCount;
    /**
     * Amount of comments this {@link Story} has received.
     */
    int commentCount;
    /**
     * Amount of likes this {@link Story} has received.
     */
    int likeCount;
    /**
     * Amount of dislikes this {@link Story} has received.
     */
    int dislikeCount;
    
    /**
     * Author of this {@link Story}.
     */
    User author;
    
    /**
     * Completion status of this {@link Story}.
     */
    Status status;
    /**
     * {@link ContentRating} of this {@link Story}.
     */
    ContentRating contentRating;
    /**
     * Categories this {@link Story} has been marked to be part of.
     */
    Set<Category> categories;
    /**
     * Main {@link Character}s of this story.
     */
    Set<Character> characters;
    /**
     * {@link Chapter}s in this story.
     */
    List<Chapter> chapters;
    /**
     * If set to <code>true</code> the sex flag has been set and this story may
     * contain such material.
     * 
     * @see #contentRating
     * @see #gore
     */
    boolean sex;
    /**
     * If set to <code>true</code> the gore flag has been set and this story may
     * contain such material.
     * 
     * @see #contentRating
     * @see #sex
     */
    boolean gore;
    
    /**
     * Favorite state of this story from the requester's viewpoint or
     * <code>null</code> if this is unknown.
     */
    @AccountSpecific FavoriteState favorited;
    /**
     * If set to <code>true</code>, the requesting user has marked this story as
     * read later, otherwise he has not or it is unknown.
     */
    @AccountSpecific boolean readLater;
    /**
     * Rating the current user has given to this story.
     */
    @AccountSpecific Rating rating;
    /**
     * Token used for rating (like / dislike) a story.
     */
    @AccountSpecific String ratingToken;
    
    /**
     * Status the author of this {@link Story} has marked this {@link Story} as.
     * Not decided by algorithms but by the author.
     */
    public static enum Status {
        /**
         * Story is being worked on and is not yet complete.
         */
        INCOMPLETE,
        /**
         * Story is complete. Sometimes additional notes might be posted
         * afterwards but mostly this means this story will not receive any more
         * updates.
         */
        COMPLETE,
        /**
         * Story has been put on hiatus but might be continued later on.
         * Typically the author will set a story to this if he has abandoned it
         * (school etc.) but says he might continue writing it at a later date.
         */
        ON_HIATUS,
        /**
         * Story has been cancelled. No updates except author's notes should be
         * expected. Rarely happens, usually on little-appreciated stories or
         * stories about controversive topics which have caused the author
         * stress, or simply stories the author did not enjoy writing as much as
         * he expected before.
         */
        CANCELLED;
    }
    
    /**
     * Content rating for this story, comparable to systems like ESRB. Initially
     * set by the author of a story but can be modified by moderators if needed.
     */
    public static enum ContentRating {
        /**
         * No restricition, contains no "bad" language (0+). Does not appear in
         * combination with a {@link Story#sex} or {@link Story#gore} tag.
         */
        EVERYONE,
        /**
         * Slight restrictions, may contain swearing (12+). If
         * {@link Story#gore} is set, the {@link Story} may contain violent
         * scenes but not in a graphical manner like {@link #MATURE}. If
         * {@link Story#sex} is set, the {@link Story} may mention sex or
         * contain slight innuendo but not in a graphical manner like
         * {@link #MATURE}. {@link Story#sex} or {@link Story#gore} are common
         * but not required.
         */
        TEEN,
        /**
         * Restricted using {@link FimFiction#allowMature} (18+). Hidden by
         * FimFiction by default. Usually (if not always) appears in combination
         * with {@link Story#sex} and/or {@link Story#gore} tags. If
         * {@link Story#gore} is set, story may contain graphical descriptions
         * of violence and/or death, if {@link Story#sex} is set it may contain
         * graphical descriptions of sex. Note that if a story matches one of
         * those descriptions it must be flagged as {@link #MATURE}, no matter
         * if it matches the other flag.
         */
        MATURE;
    }
    
    /**
     * Category (genre) this story contains. For further definitions and
     * descriptions check the <a
     * href="http://www.fimfiction.net/faq#stories">FimFiction FAQ</a>.
     */
    public static enum Category {
        /**
         * Romantic relationship (shipfic) or similar content.
         */
        ROMANCE,
        /**
         * Bad ending.
         */
        TRAGEDY,
        /**
         * Content written to sadden the reader.
         */
        SAD,
        /**
         * Generally grim environment.
         */
        DARK,
        /**
         * Comedic content that tries to amuse the reader.
         */
        COMEDY,
        /**
         * General lack of common sense and natural thinking.
         */
        RANDOM,
        /**
         * Storyline crosses events / characters with another universe (tv show,
         * game or similar).
         */
        CROSSOVER,
        /**
         * Events that affect the universe of the story.
         */
        ADVENTURE,
        /**
         * Every-day life.
         */
        SLICE_OF_LIFE,
        /**
         * Stories which contain elements which do not agree with the normal MLP
         * universe.
         */
        ALTERNATE_UNIVERSE,
        /**
         * Content with humans of any kind.
         */
        HUMAN,
        /**
         * Characters are not ponies but have a human or human-like body.
         */
        ANTHRO;
    }
    
    /**
     * Favorite status.
     */
    public static enum FavoriteState {
        /**
         * The story is not favorited.
         */
        NOT_FAVORITED,
        /**
         * The story is favorited but no e-mail notifications are enabled.
         */
        FAVORITED,
        /**
         * The story is favorited and e-mail notifiactions are enabled.
         */
        FAVORITED_EMAIL;
    }
    
    /**
     * Rating for a story.
     * 
     * @author Yawkat
     */
    public static enum Rating {
        /**
         * No decision.
         */
        NONE,
        /**
         * Liked.
         */
        LIKED,
        /**
         * Disliked.
         */
        DISLIKED;
    }
}
