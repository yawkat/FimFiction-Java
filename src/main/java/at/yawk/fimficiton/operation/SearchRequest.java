package at.yawk.fimficiton.operation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

import org.xml.sax.SAXException;

import at.yawk.fimficiton.Character;
import at.yawk.fimficiton.FimFiction;
import at.yawk.fimficiton.SearchParameters;
import at.yawk.fimficiton.Story;
import at.yawk.fimficiton.Story.Category;
import at.yawk.fimficiton.User;
import at.yawk.fimficiton.html.FullSearchParser;
import at.yawk.fimficiton.html.IdSearchParser;
import at.yawk.fimficiton.html.RssUnreadParser;

/**
 * Abstract base class for a search request. Implementations should get the
 * search page {@link #page} with the parameters {@link #parameters} from the
 * viewpoint of {@link #perspective}. If dynamic result size is possible,
 * {@link #suggestedResultCount} should be used. This is not required, access to
 * results must therefore also check the size of the result.
 * 
 * @author Yawkat
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Setter
public class SearchRequest extends AbstractRequest<List<Story>> {
    /**
     * Page index to be requested.
     */
    int page;
    /**
     * Suggested result count. Not required to be followed.
     */
    int suggestedResultCount;
    /**
     * Perspective from which the search should be performed (unread, favorites,
     * read later).
     */
    User perspective;
    /**
     * Search paramters to be given to the website.
     */
    SearchParameters parameters;
    
    @NonNull RequestMethod requestMethod = RequestMethod.ID;
    
    @Override
    protected List<Story> request(final FimFiction session) throws Exception {
        switch (this.requestMethod) {
        case ID:
            return this.requestId(session);
        case FULL:
            return this.requestFull(session);
        case UNREAD_RSS:
            return this.requestRss();
        default:
            throw new IllegalStateException();
        }
    }
    
    private List<Story> requestId(final FimFiction session) throws IOException, SAXException {
        @Cleanup final Reader reader = this.getSearchPageReader(session);
        return new IdSearchParser().parse(reader);
    }
    
    private List<Story> requestFull(final FimFiction session) throws IOException, SAXException {
        @Cleanup final Reader reader = this.getSearchPageReader(session);
        return new FullSearchParser().parse(reader);
    }
    
    private List<Story> requestRss() throws IOException, SAXException {
        if (this.perspective == null) {
            throw new IllegalStateException("User not given");
        }
        final URL feedUrl = new URL("http://www.fimfiction.net/rss/tracking.php?user=" + this.getPerspective().getId());
        @Cleanup final Reader reader = new BufferedReader(new InputStreamReader(feedUrl.openStream(), "UTF-8"));
        return new RssUnreadParser().parse(reader);
    }
    
    private Reader getSearchPageReader(final FimFiction session) throws IOException {
        final URL searchPageUrl = new URL("http://fimfiction.net/index.php?" + this.convertSearchParamsToUrlParams());
        final URLConnection connection = searchPageUrl.openConnection();
        connection.setRequestProperty("Cookie", Util.getCookies(session));
        connection.connect();
        return new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
    }
    
    @SneakyThrows(UnsupportedEncodingException.class)
    private String convertSearchParamsToUrlParams() {
        if (this.parameters == null) {
            throw new IllegalStateException("Search parameters not given");
        }
        final StringBuilder result = new StringBuilder("view=category&search=");
        result.append(URLEncoder.encode(this.getParameters().getTerm(), "UTF-8"));
        result.append("&order=");
        result.append(SearchRequestUtil.orderToParameterString(this.getParameters().getOrder()));
        for (final Category included : this.getParameters().getIncludedCategories()) {
            result.append("&tags[]=category:");
            result.append(SearchRequestUtil.categoryToParameterString(included));
        }
        for (final Category excluded : this.getParameters().getExculdedCategories()) {
            result.append("&tags[]=-category:");
            result.append(SearchRequestUtil.categoryToParameterString(excluded));
        }
        for (final Character included : this.getParameters().getIncludedCharacters()) {
            result.append("&tags[]=character:");
            result.append(SearchRequestUtil.getCharacterId(included));
        }
        for (final Character excluded : this.getParameters().getExcludedCharacters()) {
            result.append("&tags[]=-character:");
            result.append(SearchRequestUtil.getCharacterId(excluded));
        }
        result.append("&content_rating=");
        result.append(SearchRequestUtil.getContentRatingId(this.getParameters().getContentRating()));
        if (this.getParameters().isSex()) {
            result.append("&sex=1");
        }
        if (this.getParameters().isGore()) {
            result.append("&gore=1");
        }
        if (this.getParameters().isCompleted()) {
            result.append("&completed=1");
        }
        if (this.getParameters().isFavorite()) {
            result.append("&tracking");
        }
        if (this.getParameters().isUnread()) {
            result.append("&unread");
        }
        if (this.getParameters().isReadLater()) {
            result.append("&read_it_later");
        }
        result.append("&minimum_words=");
        final int minWords = this.getParameters().getMinimumWordCount();
        // substract one because fimfiction does not include the minimum word
        // count, which is required by specification
        result.append(minWords == 0 ? "" : minWords - 1);
        result.append("&maximum_words=");
        final int maxWords = this.getParameters().getMaximumWordCount();
        result.append(maxWords == 0 ? "" : maxWords);
        result.append("&page=");
        // FimFiction starts with page 1
        result.append(this.getPage() + 1);
        if (this.getPerspective() != null) {
            result.append("&user=");
            result.append(this.perspective.getId());
        }
        return result.toString();
    }
    
    /**
     * Request method to be used to parse search data. Different methods may
     * differ in speed, failure frequency and accuracy.
     */
    public static enum RequestMethod {
        /**
         * Fastest method for {@link SearchParameters}, only parses
         * {@link Story#id}. Least likely to break with FimFiction website
         * changes. Requires {@link SearchRequest#page} and
         * {@link SearchRequest#parameters}. <br>
         * <br>
         * If a {@link SearchRequest#perspective} is given, this will attempt to
         * use the given user's viewpoint. This will only work if
         * {@link SearchParameters#isFavorite()} is enabled and
         * {@link SearchParameters#isReadLater()} and
         * {@link SearchParameters#isReadLater()} are disabled. If those
         * conditions are not met the search may behave unexpectedly.
         */
        ID,
        /**
         * Slower method for {@link SearchParameters}, but includes nearly all
         * metadata in the same request. Can break easily with website changes.
         * Otherwise behaves like {@link #ID}. Requires
         * {@link SearchRequest#page} and {@link SearchRequest#parameters}. <br>
         * <br>
         * If a {@link SearchRequest#perspective} is given, this will attempt to
         * use the given user's viewpoint. This will only work if
         * {@link SearchParameters#favorite} is enabled and
         * {@link SearchParameters#readLater} and
         * {@link SearchParameters#readLater} are disabled. If those conditions
         * are not met the search may behave unexpectedly.
         */
        FULL,
        /**
         * Fastest method to get all unread favorites at once. Requires
         * {@link SearchRequest#perspective}. Only works for unread favorites,
         * user ID is required.
         * 
         * @see GetLoggedInUserOperation
         */
        UNREAD_RSS;
    }
}
