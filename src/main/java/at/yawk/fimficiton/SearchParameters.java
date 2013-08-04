package at.yawk.fimficiton;

import java.util.Collections;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Wither;
import at.yawk.fimficiton.Story.Category;
import at.yawk.fimficiton.Story.ContentRating;
import at.yawk.fimficiton.Story.Status;

/**
 * Parameters that can be understood by the FimFiction website and will be taken
 * into consideration when searching.
 * 
 * @author Yawkat
 */
@Data
@Wither
@NoArgsConstructor
@AllArgsConstructor
public class SearchParameters {
    /**
     * Search term. Exact behavior is unknown but it has been observed that this
     * appears to work like <code>{@link Story#name}.contains(term);</code> .
     */
    @NonNull String term = "";
    /**
     * {@link Order} in which search results should be listed. FimFiction
     * default is {@link Order#FIRST_POSTED_DATE} for general searches and
     * {@link Order#UPDATE_DATE} for searches like unread favorites.
     */
    @NonNull Order order = Order.FIRST_POSTED_DATE;
    /**
     * Any story returned by searches with these parameters will have all
     * categories in this {@link Set}. Make sure this does not contain any
     * entries of {@link #exculdedCategories} or behavior will be undefined.
     */
    @NonNull Set<Category> includedCategories = Collections.emptySet();
    /**
     * Any story returned by searches with these parameters will have no
     * categories in this {@link Set}. Make sure this does not contain any
     * entries of {@link #includedCategories} or behavior will be undefined.
     */
    @NonNull Set<Category> exculdedCategories = Collections.emptySet();
    /**
     * Nullable. If this is set to <code>null</code> no check will be performed
     * and all ratings will be allowed. Otherwise, all stories with this exact
     * rating will be shown.
     */
    ContentRating contentRating = null;
    /**
     * If this is set to <code>true</code> all resulting stories will have a sex
     * tag.
     */
    boolean sex = false;
    /**
     * If this is set to <code>true</code> all resulting stories will have a
     * gore tag.
     */
    boolean gore = false;
    /**
     * If this is set to <code>true</code> all resulting stories will have
     * {@link Story#getStatus()} set to {@link Status#COMPLETE}.
     */
    boolean completed = false;
    /**
     * Minimum word count, <i>including this amount</i>. If set to 0, this will
     * be ignored.
     */
    int minimumWordCount = 0;
    /**
     * Maximum word count, <i>excluding this amount</i>. If set to 0, this will
     * be ignored.
     */
    int maximumWordCount = 0;
    /**
     * If set to <code>true</code>, only stories with unread chapters will be
     * listed. If the session is not logged in this has no effect.
     */
    @AccountSpecific boolean unread = false;
    /**
     * If set to <code>true</code>, only stories that are favorited will be
     * listed. If the session is not logged in this has no effect.
     */
    @AccountSpecific boolean favorite = false;
    /**
     * If set to <code>true</code>, only stories that are on the read later list
     * will be listed. If the session is not logged in this has no effect.
     */
    @AccountSpecific boolean readLater = false;
    /**
     * Any story returned by searches with these parameters will have all
     * characters in this {@link Set}. Make sure this does not contain any
     * entries of {@link #excludedCharacters} or behavior will be undefined.
     */
    @NonNull Set<Character> includedCharacters = Collections.emptySet();
    /**
     * Any story returned by searches with these parameters will have no
     * characters in this {@link Set}. Make sure this does not contain any
     * entries of {@link #includedCharacters} or behavior will be undefined.
     */
    @NonNull Set<Character> excludedCharacters = Collections.emptySet();
    
    public static enum Order {
        /**
         * Stories which were initially posted first will come last.
         */
        FIRST_POSTED_DATE,
        /**
         * "Hot" stories are listed first. It is unknown how the algorithm for
         * this works but it is probably something about comments, likes,
         * dislikes and views per time span.
         */
        HOT,
        /**
         * Stories with the latest chapter modification or addition are listed
         * first.
         */
        UPDATE_DATE,
        /**
         * Stories with the highest like/dislike ratio are listed first. Exact
         * process is unknown but it is certain that absolute amount of likes is
         * included as well, as very few stories below 100 likes are listed far
         * up in this order.
         */
        RATING,
        /**
         * Ordered by the amount of views the chapter with the most views of
         * each story has received ({@link Story#getMaximumChapterViewCount()},
         * not {@link Story#totalViewCount}!)
         */
        VIEW_COUNT,
        /**
         * Ordered by the total amount of words the stories contain.
         */
        WORD_COUNT,
        /**
         * Ordered by the total amount of comments the stories have received.
         */
        COMMENT_COUNT;
    }
}
