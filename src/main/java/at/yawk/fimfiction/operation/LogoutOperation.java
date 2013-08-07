package at.yawk.fimfiction.operation;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import lombok.Cleanup;
import at.yawk.fimfiction.FimFiction;

/**
 * Operation to log out of the current session.
 * 
 * @author Yawkat
 */
public class LogoutOperation extends DiscardSessionOperation {
    @Override
    public void execute(final FimFiction session) throws IOException {
        final URL url = new URL(Util.BASE_URL + "/ajax/logout.php");
        final URLConnection connection = url.openConnection();
        connection.setRequestProperty("Cookie", Util.getCookies(session));
        connection.connect();
        @Cleanup final InputStream stream = new BufferedInputStream(connection.getInputStream());
        while (stream.read() > -1) {}
        super.execute(session);
    }
    
    @Override
    public void reset() {}
}
