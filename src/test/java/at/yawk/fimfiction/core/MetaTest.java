package at.yawk.fimfiction.core;

import static at.yawk.fimfiction.data.Story.StoryKey.*;
import static org.junit.Assert.assertNotNull;

import at.yawk.fimfiction.json.Serializer;
import org.junit.Test;

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
}
