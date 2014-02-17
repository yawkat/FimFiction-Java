package at.yawk.fimfiction.data;

import javax.annotation.Nonnull;

/**
 * Interface for Bundle keys. Keys have a specific value type associated with them.
 *
 * @author Jonas Konrad (yawkat)
 */
public interface Key extends Identifiable {
    @Nonnull
    ValueType getType();
}
