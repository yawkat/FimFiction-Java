package at.yawk.fimficiton;

import java.net.URL;
import java.util.Date;

import lombok.Value;
import lombok.experimental.Builder;
import lombok.experimental.Wither;

/**
 * Immutable chapter. Note that some values may be missing or <code>null</code>
 * because not all request methods support all types of metadata. New fields
 * might be added in later API versions.
 * 
 * @author Yawkat
 */
@Value
@Builder
@Wither
public class Chapter {
    /**
     * Unique ID of this chapter. This ID cannot be directly traced back to the
     * story ID but is globally unique (no chapters of other stories share the
     * same ID).
     */
    int id;
    /**
     * The URL under which this chapter can be reached.
     */
    URL url;
    /**
     * The title of this chapter.
     */
    String title;
    /**
     * The amount of words in this chapter.
     */
    int wordCount;
    /**
     * The amount of views this chapter has recieved.
     */
    int viewCount;
    /**
     * The time this chapter was posted or last modified, whichever comes later.
     * 
     * @see Date#Date(long)
     */
    long modificationDate;
    /**
     * <code>true</code> if the current logged in user has not read this chapter
     * yet, otherwise <code>false</code>. Defaults to <code>false</code> when
     * not logged in.
     */
    @AccountSpecific boolean unread;
}
