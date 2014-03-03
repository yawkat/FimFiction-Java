package at.yawk.fimfiction.core;

import at.yawk.fimfiction.data.*;
import at.yawk.fimfiction.net.NetUtil;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Builder class to search for a list of stories.
 *
 * @author Jonas Konrad (yawkat)
 */
public class Search {
    private static final byte MODE_FULL = 0;
    private static final byte MODE_ID = 1;
    private static final byte MODE_UNREAD_FEED = 2;

    @Nullable private URI uri;
    private byte mode = MODE_FULL;

    private Search() {}

    /**
     * Factory method, create a new instance.
     */
    public static Search create() { return new Search(); }

    /**
     * Sets the search URL to the given parameter.
     */
    @Nonnull
    public Search url(@Nonnull URL url) {
        Preconditions.checkNotNull(url);
        try {
            this.uri = url.toURI();
        } catch (URISyntaxException e) { throw new IllegalArgumentException(e); }
        return this;
    }

    /**
     * Set the search URL to the page of the given story.
     *
     * @see #story(at.yawk.fimfiction.data.Story)
     */
    @Nonnull
    public Search story(int story) { return url(SearchUrl.create().id(story).build()); }

    /**
     * Set the search URL to the page of the given story.
     *
     * @see #story(int)
     */
    @Nonnull
    public Search story(@Nonnull Story story) {
        Preconditions.checkNotNull(story);

        return url(SearchUrl.create().id(story).build());
    }

    /**
     * Set the search URL to the category-like search with the given parameters on the given page. Instead of making
     * subsequent calls to this method when iterating through the pages of a search,
     * using CompiledSearchParameters should be taken in consideration because it is faster.
     *
     * @see #parameters(at.yawk.fimfiction.core.SearchUrl.CompiledSearchParameters, int)
     */
    @Nonnull
    public Search parameters(@Nonnull SearchParameters parameters, int page) {
        Preconditions.checkNotNull(parameters);

        return url(SearchUrl.create().parameters(parameters).page(page).build());
    }

    /**
     * Set the search URL to the category-like search with the given parameters on the given page.
     *
     * @see #parameters(at.yawk.fimfiction.data.SearchParameters, int)
     */
    @Nonnull
    public Search parameters(@Nonnull SearchUrl.CompiledSearchParameters parameters, int page) {
        Preconditions.checkNotNull(parameters);

        return url(SearchUrl.create().parameters(parameters).page(page).build());
    }

    /**
     * Sets the search mode to MODE_FULL. This mode is (currently) the most informative one but also the most likely
     * to fail with site changes.
     */
    @Nonnull
    public Search full() {
        this.mode = MODE_FULL;
        return this;
    }

    /**
     * Sets the search mode to MODE_ID. Only returns the story IDs of the search. In the past,
     * this has been the most reliable method to not break with website changes.
     */
    @Nonnull
    public Search idOnly() {
        this.mode = MODE_ID;
        return this;
    }

    /**
     * Sets the search mode to the unread RSS feed of the given user.
     *
     * @see #unreadFeed(at.yawk.fimfiction.data.User)
     */
    @Nonnull
    public Search unreadFeed(int user) {
        this.mode = MODE_UNREAD_FEED;
        return url(NetUtil.createUrlNonNull("http://www.fimfiction.net/rss/tracking.php?user=" + user));
    }

    /**
     * Sets the search mode to the unread RSS feed of the given user.
     *
     * @see #unreadFeed(int)
     */
    public Search unreadFeed(User user) {
        Preconditions.checkNotNull(user);

        return unreadFeed(user.getInt(User.UserKey.ID));
    }

    /**
     * Performs the search and returns the result.
     */
    @Nonnull
    public SearchResult search(@Nonnull HttpClient httpClient) throws IOException, SAXException {
        Preconditions.checkNotNull(httpClient);
        Preconditions.checkState(uri != null, "Not initialized properly (missing URI)");

        HttpResponse response = NetUtil.get(httpClient, uri);

        try {
            SearchParser searchParser;

            if (mode == MODE_UNREAD_FEED) {
                searchParser = new SearchRssParser();
            } else {
                searchParser = new SearchHtmlParser();
                ((SearchHtmlParser) searchParser).idOnly = mode == MODE_ID;
            }

            XMLReader reader = new Parser();
            reader.setContentHandler(searchParser);
            reader.parse(new InputSource(response.getEntity().getContent()));

            SearchResult result = SearchResult.createMutable();
            result.set(SearchResult.SearchResultKey.STORIES, searchParser.finishedStories);
            if (searchParser instanceof SearchHtmlParser) {
                SearchHtmlParser searchHtmlParser = (SearchHtmlParser) searchParser;
                if (searchHtmlParser.loggedIn != null) {
                    result.set(SearchResult.SearchResultKey.LOGGED_IN_USER, searchHtmlParser.loggedIn);
                }
                result.set(SearchResult.SearchResultKey.LOGOUT_NONCE,
                           searchHtmlParser.nonce == null ?
                                   Optional.missing(String.class) :
                                   Optional.existing(searchHtmlParser.nonce));
            }
            return result;
        } finally {
            NetUtil.close(response);
        }
    }
}

