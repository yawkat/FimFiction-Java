package at.yawk.fimficiton.operation;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import at.yawk.fimficiton.FimFiction;

/**
 * Utility class for operations.
 * 
 * @author Yawkat
 */
class Util {
    /**
     * Name of the cookie used to store the session ID.
     */
    static final String SESSION_COOKIE_NAME = "session_token";
    
    private Util() {}
    
    /**
     * Create a new URL from the given string. String <b>must be</b> a constant
     * that <b>cannot be</b> malformed in any case.
     * {@link MalformedURLException} will be thrown silently if operation fails.
     */
    static URL toUrl(final String url) {
        try {
            return new URL(url);
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Concatenate the session cookie for the given session ID.
     */
    static String getSessionCookie(final String sessionId) {
        return SESSION_COOKIE_NAME + '=' + sessionId + ';';
    }
    
    /**
     * Concatenate the session and mature cookies.
     */
    static String getCookies(final FimFiction session) {
        final StringBuilder result = new StringBuilder();
        if (session.isAllowMature()) {
            result.append("view_mature=true;");
        }
        final String sid = session.getSessionId();
        if (sid != null) {
            result.append(getSessionCookie(sid));
        }
        return result.toString();
    }
    
    /**
     * Enable the given connection to use POST.
     * 
     * @see URLConnection#setDoInput(boolean)
     * @see URLConnection#setDoOutput(boolean)
     * @see URLConnection#setUseCaches(boolean)
     */
    static void preparePost(final URLConnection connection) {
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
    }
}
