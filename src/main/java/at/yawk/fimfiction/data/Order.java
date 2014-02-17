package at.yawk.fimfiction.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Jonas Konrad (yawkat)
 */
public enum Order implements Identifiable {
    /**
     * Stories which were initially posted first will come last.
     */
    FIRST_POSTED_DATE("first_posted_date"),
    /**
     * "Hot" stories are listed first. It is unknown how the algorithm for
     * this works but it is probably something about comments, likes,
     * dislikes and views per time span.
     */
    HOT("hot"),
    /**
     * Stories with the latest chapter modification or addition are listed
     * first.
     */
    UPDATE_DATE("update_date"),
    /**
     * Stories with the highest like/dislike ratio are listed first. Exact
     * process is unknown but it is certain that absolute amount of likes is
     * included as well, as very few stories below 100 likes are listed far
     * up in this order.
     */
    RATING("rating"),
    /**
     * Ordered by the amount of views the chapter with the most views of
     * each story has received.
     */
    VIEW_COUNT("view_count"),
    /**
     * Ordered by the total amount of words the stories contain.
     */
    WORD_COUNT("word_count"),
    /**
     * Ordered by the total amount of comments the stories have received.
     */
    COMMENT_COUNT("comment_count");

    static { IdentifiableMapper.addMapping(Order.class, values()); }

    @Nullable
    public static Order forId(@Nonnull String id) {
        return IdentifiableMapper.findIdentifiable(Order.class, id);
    }

    private final String id;

    Order(String id) {
        this.id = id;
    }

    @Nonnull
    @Override
    public String getId() {
        return id;
    }
}
