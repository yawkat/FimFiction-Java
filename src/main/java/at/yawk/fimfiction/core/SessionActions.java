package at.yawk.fimfiction.core;

import at.yawk.fimfiction.net.NetUtil;
import com.google.common.base.Preconditions;
import java.io.IOException;
import javax.annotation.Nonnull;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

/**
 * Methods for managing the current session identified by the HttpClient's cookies.
 *
 * @author Jonas Konrad (yawkat)
 */
public class SessionActions {
    private SessionActions() {}

    /**
     * Log into an account with the given credentials. Returns the login status for this request. If successful,
     * the HttpClient's cookie manager will store session cookies required for further use.
     */
    @Nonnull
    public static LoginStatus login(@Nonnull HttpClient httpClient, @Nonnull String username, @Nonnull String password)
            throws IOException {
        Preconditions.checkNotNull(httpClient);
        Preconditions.checkNotNull(username);
        Preconditions.checkNotNull(password);

        HttpResponse response = NetUtil.post(httpClient,
                                             Constants.BASE_URL + "/ajax/login.php",
                                             "keep_logged_in",
                                             "1",
                                             "username",
                                             username,
                                             "password",
                                             password);
        try {
            int firstByte = response.getEntity().getContent().read();
            switch (firstByte) {
            case '0':
                return LoginStatus.SUCCESSFUL;
            case '1':
                return LoginStatus.INVALID_PASSWORD;
            case '2':
                return LoginStatus.INVALID_USERNAME;
            default:
                return LoginStatus.UNKNOWN;
            }
        } finally {
            NetUtil.close(response);
        }
    }

    /**
     * Attempts to log out of the current account.
     * <p/>
     * <i>Due to a bug in the Fimfiction website this is not functional at the moment: Cookies will have to be
     * removed manually.</i>
     */
    public static void logout(@Nonnull HttpClient httpClient, String nonce) throws IOException {
        Preconditions.checkNotNull(httpClient);

        HttpResponse response = NetUtil.post(httpClient, Constants.BASE_URL + "/ajax/logout.php", "nonce", nonce);
        NetUtil.close(response);
    }

    public static enum LoginStatus {
        /**
         * Logged in successfully.
         */
        SUCCESSFUL,
        /**
         * Invalid username.
         */
        INVALID_USERNAME,
        /**
         * Invalid password.
         */
        INVALID_PASSWORD,
        /**
         * Unimplemented status code.
         */
        UNKNOWN,
    }
}
