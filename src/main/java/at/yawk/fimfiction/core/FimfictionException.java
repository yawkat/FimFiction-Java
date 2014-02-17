package at.yawk.fimfiction.core;

/**
 * Base class for API-specific exceptions. Most of these should not appear out of nowhere but are programming issues
 * and do not have to be caught in most cases.
 *
 * @author Jonas Konrad (yawkat)
 */
public class FimfictionException extends Error {
    public FimfictionException() {}

    public FimfictionException(String message) {
        super(message);
    }

    public FimfictionException(String message, Throwable cause) {
        super(message, cause);
    }

    public FimfictionException(Throwable cause) {
        super(cause);
    }
}
