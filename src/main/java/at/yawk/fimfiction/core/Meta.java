package at.yawk.fimfiction.core;

import at.yawk.fimfiction.data.Story;
import at.yawk.fimfiction.net.NetUtil;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.annotation.Nonnull;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.xml.sax.SAXException;

/**
 * Builder class for requesting metadata (and content) for stories. The request is considerably larger when
 * requesting content as well.
 *
 * @author Yawkat
 */
public class Meta {
    private int storyId;
    private boolean withContent;

    private Meta() {}

    /**
     * Creates a new instance.
     */
    public static Meta create() { return new Meta(); }

    /**
     * Setter for the story id.
     */
    @Nonnull
    public Meta story(int id) {
        this.storyId = id;
        return this;
    }

    /**
     * Convenience method, same as story(story.getInt(StoryKey.ID)).
     *
     * @see #story(int)
     */
    @Nonnull
    public Meta story(@Nonnull Story story) {
        Preconditions.checkNotNull(story);
        return story(story.getInt(Story.StoryKey.ID));
    }

    /**
     * Request content.
     */
    @Nonnull
    public Meta content() {
        this.withContent = true;
        return this;
    }

    /**
     * Do not request content.
     */
    @Nonnull
    public Meta noContent() {
        this.withContent = false;
        return this;
    }

    /**
     * Perform the request with the current parameters and return the result data.
     */
    @Nonnull
    public Story request(@Nonnull HttpClient httpClient) throws IOException, SAXException {
        HttpResponse response = NetUtil.get(httpClient,
                                            withContent ?
                                                    "https://www.fimfiction.net/api/v1/story/" + storyId + "?chapters" :
                                                    "https://www.fimfiction.net/api/story.php?story=" + storyId);
        try {
            JsonParser2<Story, Story.StoryKey> parser =
                    JsonParser2.story(new JsonReader(new InputStreamReader(response.getEntity().getContent(),
                                                                           Charsets.UTF_8)));
            parser.skipStatus = withContent;
            return parser.parse();
        } finally {
            NetUtil.close(response);
        }
    }
}
