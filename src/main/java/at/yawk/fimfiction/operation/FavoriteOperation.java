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
import at.yawk.fimfiction.Story.FavoriteState;

/**
 * Mark or unmark a story as read later. Returns the same story with updated
 * read later flag.
 * 
 * @author Yawkat
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class FavoriteOperation extends AbstractRequest<Story> {
    @NonNull Story story;
    @NonNull FavoriteState favoriteState;
    
    @Override
    protected Story request(final FimFiction session) throws Exception {
        switch (this.getFavoriteState()) {
        case NOT_FAVORITED:
            return this.request(session, false, false);
        case FAVORITED:
            return this.request(session, true, false);
        case FAVORITED_EMAIL:
            // we need two requests here
            this.request(session, true, false);
            return this.request(session, true, true);
        }
        throw new IllegalStateException();
    }
    
    private Story request(final FimFiction session, final boolean f, final boolean e) throws Exception {
        final FavoriteParser parser = new FavoriteParser();
        
        final URL url = new URL(Util.BASE_URL + "/ajax/add_favourite.php");
        final URLConnection connection = url.openConnection();
        Util.preparePost(connection);
        connection.setRequestProperty("Cookie", Util.getCookies(session));
        connection.connect();
        final Writer post = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        try {
            post.write("story=");
            post.write(Integer.toString(this.getStory().getId()));
            post.write("&selected=");
            post.write(f ? "1" : "0");
            post.write("&email=");
            post.write(e ? "1" : "0");
        } finally {
            post.close();
        }
        final Reader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        try {
            final XMLReader xmlReader = new Parser();
            xmlReader.setContentHandler(parser);
            xmlReader.parse(new InputSource(reader));
            if (parser.getError() != null && parser.getError().equals("You cannot perform this action any more right now")) {
                throw new SpamProtectionException();
            }
            return this.story.withFavorited(parser.isFavorite() ? parser.isEmail() ? FavoriteState.FAVORITED_EMAIL : FavoriteState.FAVORITED : FavoriteState.NOT_FAVORITED);
        } finally {
            reader.close();
        }
    }
    
    public static final class SpamProtectionException extends Exception {
        private static final long serialVersionUID = 1L;
    }
}

@Getter
@NoArgsConstructor
class FavoriteParser extends DefaultHandler {
    boolean favorite;
    boolean email;
    String error;
    
    @Getter(AccessLevel.NONE) int stage;
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) {
        if (qName.equals("selected")) {
            this.stage = 1;
        } else if (qName.equals("email")) {
            this.stage = 2;
        } else if (qName.equals("error")) {
            this.stage = 3;
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (this.stage != 0) {
            final String s = new String(ch, start, length);
            if (this.stage == 3) {
                this.error = s;
            } else {
                final int val = Integer.parseInt(s);
                switch (this.stage) {
                case 1:
                    this.favorite = val == 1;
                    break;
                case 2:
                    this.email = val == 1;
                    break;
                }
            }
            this.stage = 0;
        }
    }
}
