package at.yawk.fimfiction.operation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import at.yawk.fimfiction.FimFiction;
import at.yawk.fimfiction.Story;

/**
 * Mark or unmark a story as read later. Returns the same story with updated
 * read later flag.
 * 
 * @author Yawkat
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class ReadLaterOperation extends AbstractRequest<Story> {
    @NonNull Story story;
    boolean readLater;
    
    @Override
    protected Story request(final FimFiction session) throws Exception {
        final ReadLaterParser parser = new ReadLaterParser();
        
        final URL url = new URL(Util.BASE_URL + "/ajax/add_read_it_later.php");
        final URLConnection connection = url.openConnection();
        Util.preparePost(connection);
        connection.setRequestProperty("Cookie", Util.getCookies(session));
        connection.connect();
        final Writer post = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        try {
            post.write("story=");
            post.write(Integer.toString(this.getStory().getId()));
            post.write("&selected=");
            post.write(this.isReadLater() ? "1" : "0");
        } finally {
            post.close();
        }
        final Reader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        try {
            final XMLReader xmlReader = new Parser();
            xmlReader.setContentHandler(parser);
            xmlReader.parse(new InputSource(reader));
            return this.story.withReadLater(parser.isSelected());
        } finally {
            reader.close();
        }
    }
}

@Getter
@NoArgsConstructor
class ReadLaterParser extends DefaultHandler {
    boolean selected;
    
    @Getter(AccessLevel.NONE) int stage;
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) {
        if (qName.equals("selected")) {
            this.stage = 1;
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (this.stage != 0) {
            final int val = Integer.parseInt(new String(ch, start, length));
            switch (this.stage) {
            case 1:
                this.selected = val == 1;
                break;
            }
            this.stage = 0;
        }
    }
}
