package at.yawk.fimfiction.core;

import at.yawk.fimfiction.data.Key;

/**
 * Exception thrown when a key was missing for a request (for example the story id when attempting to load metadata
 * for it).
 *
 * @author Jonas Konrad (yawkat)
 */
public class MissingKeyException extends FimfictionException {
    private final Key missingKey;

    /**
     * Constructor.
     *
     * @param missingKey The key that was missing.
     */
    public MissingKeyException(Key missingKey) {
        this.missingKey = missingKey;
    }

    public Key getMissingKey() {
        return missingKey;
    }
}
