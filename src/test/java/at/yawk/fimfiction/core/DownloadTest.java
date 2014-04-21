package at.yawk.fimfiction.core;

import static org.junit.Assert.assertEquals;

import at.yawk.fimfiction.data.Chapter;
import at.yawk.fimfiction.data.Story;
import java.net.URL;
import org.junit.Test;

/**
 * @author Yawkat
 */
public class DownloadTest {
    @Test
    public void testGetStoryDownloadUrlText() throws Exception {
        for (int i = 0; i < 200000; i++) {
            assertEquals(new URL("https://www.fimfiction.net/download_story.php?story=" + i),
                         Download.getStoryDownloadUrl(Story.createMutable().set(Story.StoryKey.ID, i),
                                                      Download.Format.TEXT));
        }
    }

    @Test
    public void testGetStoryDownloadUrlHtml() throws Exception {
        for (int i = 0; i < 200000; i++) {
            assertEquals(new URL("https://www.fimfiction.net/download_story.php?html&story=" + i),
                         Download.getStoryDownloadUrl(Story.createMutable().set(Story.StoryKey.ID, i),
                                                      Download.Format.HTML));
        }
    }

    @Test
    public void testGetStoryDownloadUrlEpub() throws Exception {
        for (int i = 0; i < 200000; i++) {
            assertEquals(new URL("https://www.fimfiction.net/download_epub.php?story=" + i),
                         Download.getStoryDownloadUrl(Story.createMutable().set(Story.StoryKey.ID, i),
                                                      Download.Format.EPUB));
        }
    }

    @Test
    public void testGetChapterDownloadUrlText() throws Exception {
        for (int i = 0; i < 200000; i++) {
            assertEquals(new URL("https://www.fimfiction.net/download_chapter.php?chapter=" + i),
                         Download.getChapterDownloadUrl(Chapter.createMutable().set(Chapter.ChapterKey.ID, i),
                                                        Download.Format.TEXT));
        }
    }

    @Test
    public void testGetChapterDownloadUrlHtml() throws Exception {
        for (int i = 0; i < 200000; i++) {
            assertEquals(new URL("https://www.fimfiction.net/download_chapter.php?html&chapter=" + i),
                         Download.getChapterDownloadUrl(Chapter.createMutable().set(Chapter.ChapterKey.ID, i),
                                                        Download.Format.HTML));
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetChapterDownloadUrlEpub() throws Exception {
        Download.getChapterDownloadUrl(Chapter.createMutable().set(Chapter.ChapterKey.ID, 0), Download.Format.EPUB);
    }
}
