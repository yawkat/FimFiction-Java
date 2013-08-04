package at.yawk.fimfiction.html;

import java.io.Reader;

/**
 * Generic base interface that can read a given HTML stream into an object.
 * 
 * @author Yawkat
 */
public interface IHtmlParser<R> {
    /**
     * Parse the given {@link Reader}. Can only be called once before
     * {@link #reset()} must be called again.
     */
    R parse(final Reader reader) throws Exception;
    
    /**
     * Reset this parser and make it available for another call of
     * {@link #parse(Reader)}.
     */
    void reset();
}
