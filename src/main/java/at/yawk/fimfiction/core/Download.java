package at.yawk.fimfiction.core;

import at.yawk.fimfiction.data.Chapter;
import at.yawk.fimfiction.data.Story;
import at.yawk.fimfiction.net.NetUtil;
import com.google.common.base.Preconditions;
import java.net.URL;
import javax.annotation.Nonnull;

/**
 * Utility methods for building download URLs for the content of stories and chapters.
 *
 * @author Jonas Konrad (yawkat)
 */
public class Download {
    private Download() {}

    /**
     * Returns the download URL for the given story in the given format. All formats are supported at the moment.
     */
    @Nonnull
    public static URL getStoryDownloadUrl(@Nonnull Story story, @Nonnull Format format) {
        Preconditions.checkNotNull(story);
        Preconditions.checkNotNull(format);

        int id = story.get(Story.StoryKey.ID);
        switch (format) {
        case TEXT:
            return NetUtil.createUrlNonNull(Constants.BASE_URL + "/download_story.php?story=" + id);
        case HTML:
            return NetUtil.createUrlNonNull(Constants.BASE_URL + "/download_story.php?html&story=" + id);
        case EPUB:
            return NetUtil.createUrlNonNull(Constants.BASE_URL + "/download_epub.php?story=" + id);
        default:
            throw new UnsupportedOperationException("Cannot download a story as " + format);
        }
    }

    /**
     * Returns the download URL for the given chapter in the given format. EPUB is currently unsupported.
     *
     * @throws UnsupportedOperationException for EPUB.
     */
    @Nonnull
    public static URL getChapterDownloadUrl(@Nonnull Chapter chapter, @Nonnull Format format)
            throws UnsupportedOperationException {
        Preconditions.checkNotNull(chapter);
        Preconditions.checkNotNull(format);

        int id = chapter.get(Chapter.ChapterKey.ID);
        switch (format) {
        case TEXT:
            return NetUtil.createUrlNonNull(Constants.BASE_URL + "/download_chapter.php?chapter=" + id);
        case HTML:
            return NetUtil.createUrlNonNull(Constants.BASE_URL + "/download_chapter.php?html&chapter=" + id);
        default:
            throw new UnsupportedOperationException("Cannot download a story as " + format);
        }
    }

    public static enum Format {
        /**
         * Unformatted text.
         */
        TEXT,
        /**
         * Epub e-book format.
         */
        EPUB,
        /**
         * Formatted HTML.
         */
        HTML,
    }
}
