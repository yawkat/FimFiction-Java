package at.yawk.fimfiction.net;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;

/**
 * A session manager that can be used to store Fimfiction cookies and directly modify the view_mature cookie value.
 *
 * @author Jonas Konrad (yawkat)
 */
public class SessionManager {
    private final BasicClientCookie cookie = new BasicClientCookie("view_mature", "true");
    private final HttpClient httpClient;

    private SessionManager() {
        cookie.setDomain("www.fimfiction.net");

        CookieStore cookieStore = new BasicCookieStore();
        cookieStore.addCookie(cookie);
        httpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
    }

    public static SessionManager create() {return new SessionManager();}

    /**
     * Sets the mature filter value (defaults to show).
     */
    public void setMature(boolean viewMature) {
        cookie.setValue(Boolean.toString(viewMature));
    }

    /**
     * Returns the HttpClient that can be used to perform requests.
     */
    public HttpClient getHttpClient() {
        return httpClient;
    }
}
