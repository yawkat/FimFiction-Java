package at.yawk.fimficiton.operation;

import lombok.Getter;
import at.yawk.fimficiton.FimFiction;

/**
 * Any operation that returns a result of some kind.
 * 
 * @author Yawkat
 * 
 * @param <R>
 *            Type of the returned result.
 */
public abstract class AbstractRequest<R> extends AbstractOperation {
    AbstractRequest() {}
    
    @Getter private boolean resultComputed = false;
    private R result = null;
    
    @Override
    public final void execute(final FimFiction session) throws Exception {
        if (this.isResultComputed()) {
            throw new IllegalStateException("Result already computed");
        }
        this.result = this.request(session);
        this.resultComputed = true;
    }
    
    public final R getResult() {
        if (!this.isResultComputed()) {
            throw new IllegalStateException("Result not computed");
        }
        return this.result;
    }
    
    protected abstract R request(final FimFiction session) throws Exception;
    
    @Override
    public void reset() {
        this.result = null;
        this.resultComputed = false;
    }
}
