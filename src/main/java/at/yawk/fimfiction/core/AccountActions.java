package at.yawk.fimfiction.core;

import at.yawk.fimfiction.data.*;
import at.yawk.fimfiction.net.NetUtil;
import com.google.common.base.Preconditions;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nonnull;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Various user-related functions such as liking a story.
 *
 * @author Jonas Konrad (yawkat)
 */
public class AccountActions {
    private AccountActions() {}

    /**
     * Sets the read later flag for a story for this user.
     *
     * <b>Fails silently if no read later shelf is known; use the shelf API instead!</b>
     *
     * @return The modified story.
     * @deprecated Replaced with shelf API.
     */
    @Deprecated
    public static Story setReadLater(@Nonnull HttpClient httpClient, @Nonnull Story story, boolean readLater)
            throws MissingKeyException, IOException, SAXException {
        Shelf shelf = LegacySupport.findShelf(story, LegacySupport.READ_IT_LATER);
        if (shelf != null) {
            story = setShelf(httpClient, story, shelf, readLater);
        }
        return story;
    }

    /**
     * Sets the like status for a story for this user. You cannot unlike a story without disliking it at the moment.
     *
     * @return The modified flags for this story (for example the like status) without any information from the
     *         given story bundle.
     */
    @Nonnull
    public static Story setLike(@Nonnull HttpClient httpClient, @Nonnull Story story, boolean like)
            throws MissingKeyException, IOException, SAXException {
        Preconditions.checkNotNull(httpClient);
        Preconditions.checkNotNull(story);

        RatingParser parser = new RatingParser();


        HttpResponse response = NetUtil.post(httpClient,
                                             Constants.BASE_URL + "/rate.php",
                                             "story",
                                             story.get(Story.StoryKey.ID).toString(),
                                             "rating",
                                             like ? "100" : "0");

        try {
            XMLReader xmlReader = new Parser();
            xmlReader.setContentHandler(parser);
            xmlReader.parse(new InputSource(response.getEntity().getContent()));

            return Story.createMutable()
                        .set(Story.StoryKey.LIKE_COUNT, parser.likeCount)
                        .set(Story.StoryKey.DISLIKE_COUNT, parser.dislikeCount)
                        .set(Story.StoryKey.RATING,
                             parser.liked ? Rating.LIKE : parser.disliked ? Rating.DISLIKE : Rating.NONE);
        } finally {
            NetUtil.close(response);
        }
    }

    /**
     * Sets the favorite status for a story for this user.
     *
     * <b>Fails silently if no favorite shelf is known; use the shelf API instead!</b>
     *
     * @return The modified story.
     * @deprecated Replaced with shelf API.
     */
    @Deprecated
    public static Story setFavorite(@Nonnull HttpClient connectionFactory,
                                    @Nonnull Story story,
                                    @Nonnull FavoriteState state) throws IOException, SAXException {
        Shelf shelf = LegacySupport.findShelf(story, LegacySupport.FAVORITES);
        if (shelf != null) {
            story = setShelf(connectionFactory, story, shelf, state.isFavorited());
        }
        return story;
    }

    /**
     * Set the shelf status of a story (add / remove). The story is updated in-place if it's mutable.
     *
     * @return The updated story.
     */
    public static Story setShelf(@Nonnull HttpClient httpClient,
                                 @Nonnull Story story,
                                 @Nonnull Shelf shelf,
                                 boolean add) throws IOException {
        Preconditions.checkNotNull(httpClient);
        Preconditions.checkNotNull(shelf);

        HttpResponse response = NetUtil.post(httpClient,
                                             Constants.BASE_URL + "/ajax/bookshelf_items/post.php",
                                             "story",
                                             story.get(Story.StoryKey.ID).toString(),
                                             "bookshelf",
                                             shelf.get(Shelf.ShelfKey.ID).toString(),
                                             "task",
                                             add ? "add" : "remove");

        Set<Shelf> newShelves = new HashSet<Shelf>(story.<Set<Shelf>>get(Story.StoryKey.SHELVES_ADDED));

        try {
            JsonReader reader = new JsonReader(new InputStreamReader(response.getEntity().getContent()));
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("added")) {
                    boolean added = reader.nextBoolean();
                    for (Iterator<Shelf> iterator = newShelves.iterator(); iterator.hasNext(); ) {
                        Shelf shel = iterator.next();
                        if (shel.getInt(Shelf.ShelfKey.ID) == shelf.getInt(Shelf.ShelfKey.ID)) {
                            iterator.remove();
                        }
                    }
                    if (added) {
                        newShelves.add(shelf);
                    }
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } finally {
            NetUtil.close(response);
        }

        Story newStory = story.mutableVersion();
        newStory.set(Story.StoryKey.SHELVES_ADDED, newShelves);
        LegacySupport.deriveFavoriteAndReadLaterFromShelves(newStory);
        return newStory;
    }

    /**
     * Toggles the read status for a chapter for this user. Explicitly setting it to a specific flag is impossible at
     * the moment.
     *
     * @return The modified flags for this chapter (for example the unread status) without any information from the
     *         given chapter bundle.
     */
    public static Chapter toggleRead(@Nonnull HttpClient httpClient, @Nonnull Chapter chapter)
            throws IOException, SAXException {
        Preconditions.checkNotNull(httpClient);
        Preconditions.checkNotNull(chapter);

        HttpResponse response = NetUtil.post(httpClient,
                                             Constants.BASE_URL + "/ajax/toggle_read.php",
                                             "chapter",
                                             chapter.get(Chapter.ChapterKey.ID).toString());

        try {
            XMLReader r = new Parser();
            UnreadParser parser = new UnreadParser();
            r.setContentHandler(parser);
            r.parse(new InputSource(response.getEntity().getContent()));
            return Chapter.createMutable().set(Chapter.ChapterKey.UNREAD, parser.unread);
        } finally {
            NetUtil.close(response);
        }
    }
}

class RatingParser extends DefaultHandler {
    int likeCount;
    int dislikeCount;
    boolean liked;
    boolean disliked;

    int stage;

    @Override
    public void startElement(@Nonnull String uri,
                             @Nonnull String localName,
                             @Nonnull String qName,
                             @Nonnull Attributes attributes) {
        if ("likes".equals(qName)) {
            this.stage = 1;
        } else if ("dislikes".equals(qName)) {
            this.stage = 2;
        } else if ("liked".equals(qName)) {
            this.stage = 3;
        } else if ("disliked".equals(qName)) {
            this.stage = 4;
        }
    }

    @Override
    public void characters(@Nonnull char[] ch, int start, int length) throws SAXException {
        if (this.stage != 0) {
            int val = Integer.parseInt(new String(ch, start, length));
            switch (this.stage) {
            case 1:
                this.likeCount = val;
                break;
            case 2:
                this.dislikeCount = val;
                break;
            case 3:
                this.liked = val == 1;
                break;
            case 4:
                this.disliked = val == 1;
                break;
            }
            this.stage = 0;
        }
    }
}

class UnreadParser extends DefaultHandler {
    boolean unread;

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (length == 1) {
            if (ch[start] == '1' || ch[start] == '0') {
                unread = ch[start] == '0';
            }
        }
    }
}
