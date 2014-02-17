package at.yawk.fimfiction.net;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * Various helpers utilities for HTTP networking.
 *
 * @author Jonas Konrad (yawkat)
 */
public class NetUtil {
    private NetUtil() {}

    /**
     * Use UTF-8 to URLEncode a String without throwing an exception.
     */
    @Nonnull
    public static String encodeUtf8(@Nonnull String input) {
        Preconditions.checkNotNull(input);
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // this should never happen
            throw new RuntimeException(e);
        }
    }

    /**
     * URLEncode a POST body as UTF-8.
     */
    @Nonnull
    private static String encodePost(@Nonnull String input) {
        return encodeUtf8(input);
    }

    /**
     * Silently close a HttpResponse.
     */
    public static void close(@Nonnull HttpResponse response) {
        Preconditions.checkNotNull(response);
        EntityUtils.consumeQuietly(response.getEntity());
    }

    /**
     * Builds a URL out of a String or returns null if it is malformed.
     */
    @Nullable
    public static URL createUrl(@Nonnull String url) {
        Preconditions.checkNotNull(url);
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Build a URL out of a String. Behaviour for malformed input is undefined so this should only be used with
     * hardcoded URLs.
     */
    @Nonnull
    public static URL createUrlNonNull(@Nonnull String url) {
        URL res = createUrl(url);
        assert res != null;
        return res;
    }

    /**
     * Perform a HTTP request to a URI.
     */
    public static HttpResponse get(HttpClient client, String uri) throws IOException {
        return client.execute(new HttpGet(uri));
    }

    /**
     * Perform a HTTP request to a URI.
     */
    public static HttpResponse get(HttpClient client, URI uri) throws IOException {
        return client.execute(new HttpGet(uri));
    }

    /**
     * Perform a POST request to a URI with the given name-value pairs as the body. Even indexes of the properties
     * array are counted as keys, odd indexes as values of the key before them. The property array must contain an
     * even amount of Strings.
     */
    public static HttpResponse post(HttpClient client, String uri, String... properties) throws IOException {
        List<NameValuePair> body = Lists.newArrayList();
        Preconditions.checkNotNull(properties);
        Preconditions.checkArgument((properties.length % 2) == 0);
        for (int i = 0; i < properties.length; i += 2) {
            Preconditions.checkNotNull(properties[i]);
            body.add(new BasicNameValuePair(properties[i], properties[i + 1]));
        }

        HttpPost post = new HttpPost(uri);
        post.setEntity(new UrlEncodedFormEntity(body, Charsets.UTF_8));
        return client.execute(post);
    }
}
