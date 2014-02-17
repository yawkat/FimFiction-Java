package at.yawk.fimfiction.core;

import at.yawk.fimfiction.data.Chapter;
import at.yawk.fimfiction.data.FavoriteState;
import at.yawk.fimfiction.data.Rating;
import at.yawk.fimfiction.data.Story;
import at.yawk.fimfiction.net.NetUtil;
import com.google.common.base.Preconditions;
import java.io.IOException;
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
     * @return The modified flags for this story (for example the read later status) without any information from the
     *         given story bundle.
     */
    public static Story setReadLater(@Nonnull HttpClient httpClient, @Nonnull Story story, boolean readLater)
            throws MissingKeyException, IOException, SAXException {
        Preconditions.checkNotNull(httpClient);
        Preconditions.checkNotNull(story);


        HttpResponse response = NetUtil.post(httpClient,
                                             "http://www.fimfiction.net/ajax/add_read_it_later.php",
                                             "story",
                                             story.get(Story.StoryKey.ID).toString(),
                                             "selected",
                                             readLater ? "1" : "0");

        ReadLaterParser parser = new ReadLaterParser();
        try {
            XMLReader xmlReader = new Parser();
            xmlReader.setContentHandler(parser);
            xmlReader.parse(new InputSource(response.getEntity().getContent()));

            return Story.createMutable().set(Story.StoryKey.READ_LATER_STATE, parser.selected);
        } finally {
            NetUtil.close(response);
        }
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
                                             "http://www.fimfiction.net/rate.php",
                                             "story",
                                             story.get(Story.StoryKey.ID).toString(),
                                             "rating",
                                             like ? "100" : "0",
                                             "ip",
                                             story.get(Story.StoryKey.RATING_TOKEN).toString());

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
     * Sets the favorite status for a story for this user. This might send multiple HTTP requests to finish.
     *
     * @return The modified flags for this story (for example the favorite status) without any information from the
     *         given story bundle.
     */
    public static Story setFavorite(@Nonnull HttpClient connectionFactory,
                                    @Nonnull Story story,
                                    @Nonnull FavoriteState state) throws IOException, SAXException {
        if (state == FavoriteState.FAVORITED_WITH_EMAIL) {
            setFavorite0(connectionFactory, story, FavoriteState.FAVORITED);
            return setFavorite0(connectionFactory, story, FavoriteState.FAVORITED_WITH_EMAIL);
        } else {
            return setFavorite0(connectionFactory, story, state);
        }
    }

    @Nonnull
    private static Story setFavorite0(@Nonnull HttpClient httpClient,
                                      @Nonnull Story story,
                                      @Nonnull FavoriteState state)
            throws MissingKeyException, IOException, SAXException {
        Preconditions.checkNotNull(httpClient);
        Preconditions.checkNotNull(story);
        Preconditions.checkNotNull(state);

        FavoriteParser parser = new FavoriteParser();

        HttpResponse response = NetUtil.post(httpClient,
                                             "http://www.fimfiction.net/ajax/add_favourite.php",
                                             "story",
                                             story.get(Story.StoryKey.ID).toString(),
                                             "selected",
                                             state.isFavorited() ? "1" : "0",
                                             "email",
                                             state == FavoriteState.FAVORITED_WITH_EMAIL ? "1" : "0");

        try {
            XMLReader xmlReader = new Parser();
            xmlReader.setContentHandler(parser);
            xmlReader.parse(new InputSource(response.getEntity().getContent()));


            return Story.createMutable()
                        .set(Story.StoryKey.FAVORITE_STATE,
                             parser.favorite ?
                                     parser.email ? FavoriteState.FAVORITED_WITH_EMAIL : FavoriteState.FAVORITED :
                                     FavoriteState.NOT_FAVORITED);
        } finally {
            NetUtil.close(response);
        }
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
                                             "http://www.fimfiction.net/ajax/toggle_read.php",
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

class ReadLaterParser extends DefaultHandler {
    boolean selected;

    int stage;

    @Override
    public void startElement(@Nonnull String uri,
                             @Nonnull String localName,
                             @Nonnull String qName,
                             @Nonnull Attributes attributes) {
        if ("selected".equals(qName)) {
            this.stage = 1;
        }
    }

    @Override
    public void characters(@Nonnull char[] ch, int start, int length) {
        if (this.stage != 0) {
            int val = Integer.parseInt(new String(ch, start, length));
            switch (this.stage) {
            case 1:
                this.selected = val == 1;
                break;
            }
            this.stage = 0;
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

class FavoriteParser extends DefaultHandler {
    boolean favorite;
    boolean email;
    String error;

    int stage;

    @Override
    public void startElement(@Nonnull String uri,
                             @Nonnull String localName,
                             @Nonnull String qName,
                             @Nonnull Attributes attributes) {
        if ("selected".equals(qName)) {
            this.stage = 1;
        } else if ("email".equals(qName)) {
            this.stage = 2;
        } else if ("error".equals(qName)) {
            this.stage = 3;
        }
    }

    @Override
    public void characters(@Nonnull char[] ch, int start, int length) throws SAXException {
        if (this.stage != 0) {
            String s = new String(ch, start, length);
            if (this.stage == 3) {
                this.error = s;
            } else {
                int val = Integer.parseInt(s);
                switch (this.stage) {
                case 1:
                    this.favorite = val == 1;
                    break;
                case 2:
                    this.email = val == 1;
                    break;
                }
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
