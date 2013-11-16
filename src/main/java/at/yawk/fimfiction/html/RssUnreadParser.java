package at.yawk.fimfiction.html;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class for parsing RSS data into a list of unread favorites.
 * 
 * @author Yawkat
 */
public class RssUnreadParser extends AbstractSearchParser {
    private static final int NONE = 0;
    private static final int NONE_STORY = 1;
    private static final int TITLE = 2;
    private static final int DESCRIPTION = 3;
    private static final int LINK = 4;
    private static final int UPDATE_DATE = 5;
    
    private final SimpleDateFormat rssDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HHmmssZ", Locale.ENGLISH);
    private int stage = NONE;
    private StringBuilder descriptionBuilder;
    
    @Override
    public void reset() {
        super.reset();
        this.descriptionBuilder = null;
        this.stage = NONE;
    }
    
    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) {
        if (this.stage == NONE) {
            if (qName.equals("item")) {
                this.startStory();
                this.stage = NONE_STORY;
            }
        } else if (this.stage == NONE_STORY) {
            if (qName.equals("title")) {
                this.stage = TITLE;
            } else if (qName.equals("description")) {
                this.stage = DESCRIPTION;
                this.descriptionBuilder = new StringBuilder();
            } else if (qName.equals("link")) {
                this.stage = LINK;
            } else if (qName.equals("pubDate")) {
                this.stage = UPDATE_DATE;
            }
        } else if (this.stage == DESCRIPTION) {
            this.descriptionBuilder.append("<" + qName + ">");
        }
    }
    
    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) {
        switch (this.stage) {
        case NONE_STORY:
            if (qName.equals("item")) {
                this.endStory();
                this.stage = NONE;
            }
            break;
        case DESCRIPTION:
            if (qName.equals("description")) {
                this.getCurrentBuilder().description(this.descriptionBuilder.toString());
                this.descriptionBuilder = null;
                this.stage = NONE_STORY;
            }
            break;
        case NONE:
        case LINK:
            break;
        default:
            this.stage = NONE_STORY;
            break;
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        // using this might cause problems in specific situations because the
        // actual text between start and end tag could be divided into multiple
        // calls of this method, but so far this only happened to me for
        // description which is concatenated anyway.
        final String asString = new String(ch, start, length);
        switch (this.stage) {
        case TITLE:
            this.getCurrentBuilder().title(asString);
            this.stage = NONE_STORY;
            break;
        case DESCRIPTION:
            this.descriptionBuilder.append(asString);
            break;
        case LINK:
            try {
                this.getCurrentBuilder().url(new URL(asString));
                final String sub1 = asString.substring(asString.indexOf('y') + 2);
                final String sub2 = sub1.substring(0, sub1.indexOf('/'));
                this.getCurrentBuilder().id(Integer.parseInt(sub2));
            } catch (final MalformedURLException e) {
                throw new SAXException(e);
            }
            this.stage = NONE_STORY;
            break;
        case UPDATE_DATE:
            try {
                this.getCurrentBuilder().modificationDate(this.rssDateFormat.parse(asString.replace(":", "")).getTime());
            } catch (final ParseException e) {
                throw new SAXException(e);
            }
            this.stage = NONE_STORY;
            break;
        }
    }
}
