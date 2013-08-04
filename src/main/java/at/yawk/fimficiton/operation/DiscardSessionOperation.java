package at.yawk.fimficiton.operation;

import java.io.IOException;

import at.yawk.fimficiton.FimFiction;

/**
 * Operation to discard the current session ID. <b>This does not log out the
 * user, the session ID is still valid. Use {@link LogoutOperation} for clean
 * logoff.</b>
 * 
 * @author Yawkat
 */
public class DiscardSessionOperation extends AbstractOperation {
    @Override
    public void execute(final FimFiction session) throws IOException {
        session.setSessionId(null);
    }
    
    @Override
    public void reset() {}
}
