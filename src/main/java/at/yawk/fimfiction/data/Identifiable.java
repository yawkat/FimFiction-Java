package at.yawk.fimfiction.data;

import javax.annotation.Nonnull;

/**
 * Interface for different bundle keys or values that are uniquely identifiable. The #getId method return value
 * should be constant and unique among the specific Identifiable. However, different Identifiable types may share IDs
 * such as "url".
 *
 * @author Jonas Konrad (yawkat)
 */
public interface Identifiable {
    @Nonnull
    String getId();
}
