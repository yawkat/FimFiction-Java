package at.yawk.fimfiction.operation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

import lombok.Cleanup;
import at.yawk.fimfiction.FimFiction;
import at.yawk.fimfiction.User;
import at.yawk.fimfiction.html.UserIdFinder;

/**
 * Operation for getting the current logged in user.Returns either the current
 * logged in user or <code>null</code> if the user is not logged in (anymore).
 * 
 * @author Yawkat
 */
public class GetLoggedInUserOperation extends AbstractRequest<User> {
    @Override
    protected User request(final FimFiction session) throws Exception {
        final URL requestUrl = new URL("http://fimfiction.net/");
        final URLConnection connection = requestUrl.openConnection();
        connection.setRequestProperty("Cookie", Util.getCookies(session));
        connection.connect();
        @Cleanup final Reader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        return new UserIdFinder().parse(reader);
    }
}
