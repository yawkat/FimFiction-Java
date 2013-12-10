package at.yawk.fimfiction.operation;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import at.yawk.fimfiction.Chapter;
import at.yawk.fimfiction.FimFiction;

/**
 * Operation to toggle read status of a chapter. Currently it is impossible to
 * set it to <code>true</code> or <code>false</code> specifically.
 * 
 * @author Yawkat
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class ToggleReadStatusOperation extends AbstractRequest<Chapter> {
    @NonNull Chapter chapter;
    
    @Override
    protected Chapter request(final FimFiction session) throws Exception {
        final URL rateUrl = new URL(Util.BASE_URL + "/ajax/toggle_read.php");
        final URLConnection connection = rateUrl.openConnection();
        Util.preparePost(connection);
        connection.setRequestProperty("Cookie", Util.getCookies(session));
        connection.connect();
        final Writer post = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        try {
            post.write("chapter=");
            post.write(Integer.toString(this.getChapter().getId()));
        } finally {
            post.close();
        }
        
        final InputStream stream = connection.getInputStream();
        try {
            final XMLReader r = new Parser();
            final AtomicBoolean unread = new AtomicBoolean(this.chapter.isUnread());
            r.setContentHandler(new DefaultHandler() {
                @Override
                public void characters(final char[] ch, final int start, final int length) throws SAXException {
                    if (length == 1) {
                        if (ch[start] == '1' || ch[start] == '0') {
                            unread.set(ch[start] == '0');
                        }
                    }
                }
            });
            r.parse(new InputSource(stream));
            return this.chapter.withUnread(unread.get());
        } finally {
            stream.close();
        }
    }
}
