package at.yawk.fimfiction.operation;

import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import lombok.EqualsAndHashCode;
import lombok.Value;
import at.yawk.fimfiction.Story;

/**
 * Download an entire story.
 * 
 * @author Yawkat
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class StoryDownloadOperation extends AbstractDownloadOperation {
    Story story;
    
    public StoryDownloadOperation(final OutputStream output, final DownloadType downloadType, final Story story) {
        super(output, downloadType);
        this.story = story;
    }
    
    @Override
    protected URL getDownloadUrl() throws MalformedURLException {
        switch (this.getDownloadType()) {
        case EPUB:
            return new URL("http://fimfiction.net/download_epub.php?story=" + this.getStory().getId());
        case HTML:
            return new URL("http://fimfiction.net/download_story.php?html&story=" + this.getStory().getId());
        case TEXT:
            return new URL("http://fimfiction.net/download_story.php?story=" + this.getStory().getId());
        default:
            throw new IllegalArgumentException("Unknown download type: " + this.getDownloadType().toString());
        }
    }
}
