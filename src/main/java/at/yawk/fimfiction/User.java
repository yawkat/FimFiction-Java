package at.yawk.fimfiction;

import java.net.URL;

import lombok.Value;
import lombok.experimental.Builder;
import lombok.experimental.Wither;

/**
 * Immutable user. Note that some values may be missing or <code>null</code>
 * because not all request methods support all types of metadata. New versions
 * might be added in later api versions.
 * 
 * @author Yawkat
 */
@Value
@Builder
@Wither
public class User {
    /**
     * Unique ID of the {@link User}.
     */
    int id;
    /**
     * Username of the {@link User}. Appears to be unique at the moment but
     * {@link #id} should be preferred for persistent storage and verification.
     */
    String name;
    /**
     * {@link URL} of the profile image of this user.
     */
    URL profileImageUrl;
}
