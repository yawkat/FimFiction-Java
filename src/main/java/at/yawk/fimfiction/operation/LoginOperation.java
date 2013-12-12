package at.yawk.fimfiction.operation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import at.yawk.fimfiction.FimFiction;

/**
 * Operation for logging into FimFiction with given credentials.
 * 
 * @author Yawkat
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class LoginOperation extends AbstractRequest<LoginOperation.Result> {
    /**
     * {@link URL} used for requesting session ID.
     */
    private static final URL LOGIN_URL = Util.toUrl(Util.BASE_URL + "/ajax/login.php");
    
    /**
     * Username to be logged in.
     */
    @NonNull String username;
    /**
     * Password of the user to be logged in.
     */
    @NonNull String password;
    
    @Override
    protected Result request(final FimFiction session) throws IOException {
        // start
        final URLConnection connection = LOGIN_URL.openConnection();
        // prepare for POST parameters
        Util.preparePost(connection);
        // connect
        connection.connect();
        // send post parameters
        final OutputStream post = connection.getOutputStream();
        try {
            // username & password, escaped
            final StringBuilder message = new StringBuilder("keep_logged_in=1&username=");
            message.append(URLEncoder.encode(this.username, "UTF-8"));
            message.append("&password=");
            message.append(URLEncoder.encode(this.password, "UTF-8"));
            post.write(message.toString().getBytes("UTF-8"));
        } finally {
            post.close();
        }
        // read first byte of reply
        @Cleanup final InputStream answer = connection.getInputStream();
        final int firstByte = answer.read();
        switch (firstByte) {
        case '1':
            // 1 = invalid password
            return Result.INVALID_PASSWORD;
        case '2':
            // 2 = invalid username
            return Result.INVALID_USERNAME;
        case '0':
            // 0 = logged in
            final List<String> cookieFields = connection.getHeaderFields().get("Set-Cookie");
            // check if any cookies were set at all
            if (cookieFields != null) {
                // cookie ID we are searching for
                final String sessionCookiePrefix = Util.SESSION_COOKIE_NAME + '=';
                for (final String cookie : cookieFields) {
                    if (cookie.startsWith(sessionCookiePrefix)) {
                        // remove key part and trailing metadata (expiration
                        // date, path etc)
                        final String sessionId = cookie.substring(sessionCookiePrefix.length(), cookie.indexOf(';'));
                        // save
                        session.setSessionId(sessionId);
                        // exit
                        return Result.SUCCESS;
                    }
                }
            }
        default:
            // unknown result (probably -1, no connection)
            return Result.UNKNOWN;
        }
    }
    
    public static enum Result {
        /**
         * Successfully logged in.
         */
        SUCCESS,
        /**
         * Password not valid.
         */
        INVALID_PASSWORD,
        /**
         * Username not valid.
         */
        INVALID_USERNAME,
        /**
         * Default state when either an invalid status code was received or no
         * session ID was sent even though the status code says the login data
         * was correct
         */
        UNKNOWN;
    }
}
