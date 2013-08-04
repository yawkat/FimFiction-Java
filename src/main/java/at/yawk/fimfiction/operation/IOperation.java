package at.yawk.fimfiction.operation;

import at.yawk.fimfiction.FimFiction;

/**
 * General operation that can be performed on a {@link FimFiction} session.
 * 
 * @author Yawkat
 */
public interface IOperation {
    /**
     * Execute this operation.
     * 
     * @throws IllegalStateException
     *             if this operation has been executed before and
     *             {@link #reset()} has not yet been called.
     */
    void execute(final FimFiction session) throws Exception;
    
    /**
     * Reset any data computed and allow another call of
     * {@link #execute(FimFiction)}.
     */
    void reset();
}
