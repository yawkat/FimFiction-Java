package at.yawk.fimfiction.core;

import static at.yawk.fimfiction.data.Story.StoryKey.*;
import static org.junit.Assert.assertNotNull;

import at.yawk.fimfiction.data.FormattedString;
import at.yawk.fimfiction.data.SearchResult;
import at.yawk.fimfiction.data.Story;
import at.yawk.fimfiction.json.Serializer;
import java.io.IOException;
import java.util.List;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * @author Yawkat
 */
public class MetaTest {
    @Test
    public void testCreate() throws Exception {
        assertNotNull(Meta.create());
        System.out.println(new Serializer().serializeBundle(Meta.create().story(10).request(TestUtil.getClient())));
    }

    @Test
    public void testStoryObj() throws Exception {
        TestStory.check(Meta.create().story(TestStory.getInstance()).request(TestUtil.getClient()),
                        URL_THUMBNAIL,
                        ID,
                        AUTHOR,
                        TITLE,
                        DESCRIPTION_SHORT,
                        DESCRIPTION,
                        WORD_COUNT,
                        CONTENT_RATING,
                        CHAPTER_COUNT,
                        STATUS,
                        URL,
                        DATE_UPDATED,
                        CATEGORIES);
    }

    @Test
    public void testDescription() throws IOException, SAXException {
        SearchResult result = Search.create().story(106887).full().search(TestUtil.getClient());
        System.out.println("'" + result.<List<Story>>get(SearchResult.SearchResultKey.STORIES)
                                       .get(0)
                                       .<FormattedString>get(DESCRIPTION)
                                       .buildFormattedText(FormattedString.Markup.HTML) + "'"
        );
    }
}
