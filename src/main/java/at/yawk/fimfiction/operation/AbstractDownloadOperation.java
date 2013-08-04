package at.yawk.fimfiction.operation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;
import at.yawk.fimfiction.FimFiction;

/**
 * Abstract base class for downloading content.
 * 
 * @author Yawkat
 */
@Value
@NonFinal
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractDownloadOperation extends AbstractOperation {
    @NonNull OutputStream output;
    @NonNull DownloadType downloadType;
    
    @Override
    public void execute(final FimFiction session) throws IOException {
        @Cleanup final InputStream input = this.getDownloadUrl().openStream();
        final byte[] buffer = new byte[1024];
        int length;
        while ((length = input.read(buffer)) > 0) {
            this.getOutput().write(buffer, 0, length);
        }
    }
    
    /**
     * Get the {@link URL} to be downloaded.
     */
    protected abstract URL getDownloadUrl() throws MalformedURLException;
    
    @Override
    public void reset() {}
    
    public static enum DownloadType {
        EPUB,
        TEXT,
        HTML;
    }
}
