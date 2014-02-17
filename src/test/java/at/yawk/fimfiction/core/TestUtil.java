package at.yawk.fimfiction.core;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * @author Yawkat
 */
public class TestUtil {
    private static final HttpClient client = HttpClientBuilder.create().build();

    public static HttpClient getClient() {
        return client;
    }
}
