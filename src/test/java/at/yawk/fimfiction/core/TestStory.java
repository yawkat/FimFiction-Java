package at.yawk.fimfiction.core;

import static org.junit.Assert.assertEquals;

import at.yawk.fimfiction.data.Story;
import at.yawk.fimfiction.json.Deserializer;
import com.google.common.base.Charsets;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author Yawkat
 */
public class TestStory {
    private TestStory() {}

    private static final Story instance;

    static {
        InputStream stream = TestStory.class.getResourceAsStream("test_story.json");
        Reader reader = new InputStreamReader(stream, Charsets.UTF_8);
        JsonElement element = new JsonParser().parse(reader);
        try {
            instance = new Deserializer().deserializeBundle(element.getAsJsonObject(), Story.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Story getInstance() {
        return instance;
    }

    public static void check(Story actual, Story.StoryKey... expectedKeys) {
        for (Story.StoryKey key : expectedKeys) {
            Object e = getInstance().get(key);
            Object a = actual.get(key);
            if (e instanceof Number) { e = ((Number) e).doubleValue(); }
            if (a instanceof Number) { a = ((Number) a).doubleValue(); }
            assertEquals(e, a);
        }
    }
}
