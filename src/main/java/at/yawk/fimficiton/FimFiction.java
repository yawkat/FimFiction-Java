package at.yawk.fimficiton;

import lombok.Getter;
import lombok.Setter;
import at.yawk.fimficiton.Story.ContentRating;
import at.yawk.fimficiton.operation.IOperation;
import at.yawk.fimficiton.operation.LoginOperation;

/**
 * Session class for the API. Stores session data and is required to execute and
 * {@link IOperation}.
 * 
 * @author Yawkat
 * @see LoginOperation
 */
public class FimFiction {
    /**
     * Initialized as <code>null</code> but may be changed by logging in or
     * setting it manually. Alphanumeric {@link String} if logged in.
     */
    @Getter @Setter private String sessionId = null;
    /**
     * If set to <code>false</code> all stories with a
     * {@link Story#contentRating} of {@link ContentRating#MATURE} will be
     * hidden. Defaulting to <code>true</code> because this is usually the
     * wanted behavior.
     */
    @Getter @Setter private boolean allowMature = true;
    
    /**
     * Execute the given operation with this session data.
     */
    public void executeOperation(final IOperation operation) throws Exception {
        operation.execute(this);
    }
}
