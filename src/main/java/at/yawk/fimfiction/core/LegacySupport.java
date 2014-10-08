package at.yawk.fimfiction.core;

import at.yawk.fimfiction.data.FavoriteState;
import at.yawk.fimfiction.data.Shelf;
import at.yawk.fimfiction.data.Story;
import java.util.Set;
import javax.annotation.Nullable;

import static at.yawk.fimfiction.data.Story.StoryKey.*;

/**
 * @author yawkat
 */
@SuppressWarnings("deprecation")
class LegacySupport {
    static final String FAVORITES = "Favourites";
    static final String READ_IT_LATER = "Read It Later";

    static void deriveFavoriteAndReadLaterFromShelves(Story mutableStory) {
        Boolean favorite = isInShelf(mutableStory, FAVORITES);
        Boolean readLater = isInShelf(mutableStory, READ_IT_LATER);
        if (favorite != null) {
            mutableStory.set(FAVORITE_STATE, favorite ? FavoriteState.FAVORITED : FavoriteState.NOT_FAVORITED);
        }
        if (readLater != null) {
            mutableStory.set(READ_LATER_STATE, readLater);
        }
    }

    @Nullable
    static Boolean isInShelf(Story story, String shelfName) {
        for (Shelf shelf : story.<Set<Shelf>>get(SHELVES_ADDED)) {
            if (shelfName.equals(shelf.getString(Shelf.ShelfKey.NAME))) {
                return true;
            }
        }
        for (Shelf shelf : story.<Set<Shelf>>get(SHELVES_NOT_ADDED)) {
            if (shelfName.equals(shelf.getString(Shelf.ShelfKey.NAME))) {
                return false;
            }
        }
        return null;
    }

    @Nullable
    static Shelf findShelf(Story story, String shelfName) {
        for (Shelf shelf : story.<Set<Shelf>>get(SHELVES_ADDED)) {
            if (shelfName.equals(shelf.getString(Shelf.ShelfKey.NAME))) {
                return shelf;
            }
        }
        for (Shelf shelf : story.<Set<Shelf>>get(SHELVES_NOT_ADDED)) {
            if (shelfName.equals(shelf.getString(Shelf.ShelfKey.NAME))) {
                return shelf;
            }
        }
        return null;
    }
}
