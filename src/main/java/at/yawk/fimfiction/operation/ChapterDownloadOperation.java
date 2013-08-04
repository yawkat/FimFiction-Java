package at.yawk.fimfiction.operation;

import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import lombok.EqualsAndHashCode;
import lombok.Value;
import at.yawk.fimfiction.Chapter;

/**
 * Operation to download a chapter.
 * 
 * @author Yawkat
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class ChapterDownloadOperation extends AbstractDownloadOperation {
    Chapter chapter;
    
    public ChapterDownloadOperation(final OutputStream output, final DownloadType downloadType, final Chapter chapter) {
        super(output, downloadType);
        this.chapter = chapter;
        
        if (downloadType == DownloadType.EPUB) {
            throw new IllegalArgumentException("Cannot download chapter as EPUB.");
        }
    }
    
    @Override
    protected URL getDownloadUrl() throws MalformedURLException {
        switch (this.getDownloadType()) {
        case HTML:
            return new URL("http://fimfiction.net/download_chapter.php?html&chapter=" + this.getChapter().getId());
        case TEXT:
            return new URL("http://fimfiction.net/download_chapter.php?chapter=" + this.getChapter().getId());
        default:
            throw new IllegalArgumentException("Unknown download type: " + this.getDownloadType().toString());
        }
    }
}
