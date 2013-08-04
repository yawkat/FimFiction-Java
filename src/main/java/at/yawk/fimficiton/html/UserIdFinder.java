package at.yawk.fimficiton.html;

import java.io.Reader;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import at.yawk.fimficiton.User;

/**
 * Class for parsing any (!) FimFiciton frontend site and getting the current
 * user ID.
 * 
 * @author Yawkat
 */
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserIdFinder extends DefaultHandler implements IHtmlParser<User> {
    User user;
    boolean done;
    
    /**
     * Parses input from the given {@link Reader}. Returns either
     * <code>null</code> if the user is not logged in (anymore) or a
     * {@link User} object with at least the {@link User#id} field set.
     */
    @Override
    public User parse(final Reader reader) throws Exception {
        final XMLReader xmlReader = new Parser();
        xmlReader.setContentHandler(this);
        xmlReader.parse(new InputSource(reader));
        return this.getUser();
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) {
        if (this.isDone()) {
            return;
        }
        final String asString = new String(ch, start, length);
        if (asString.indexOf("var static_url = \"//www.fimfiction-static.net\";") > -1) {
            final int i = asString.indexOf("logged_in_user.id = ");
            if (i == -1) {
                this.setUser(null);
            } else {
                final String sub1 = asString.substring(i + 20);
                final String sub2 = sub1.substring(0, sub1.indexOf(';'));
                this.setUser(User.builder().id(Integer.parseInt(sub2)).build());
            }
            this.setDone(true);
        }
    }
    
    @Override
    public void reset() {
        this.setUser(null);
    }
}
