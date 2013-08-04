package at.yawk.fimficiton.html;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class for finding story IDs in search HTML.
 * 
 * @author Yawkat
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IdSearchParser extends AbstractSearchParser {
    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) boolean afterH2;
    
    @Override
    public void reset() {
        super.reset();
        this.setAfterH2(false);
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        if (this.isAfterH2()) {
            if (qName.equals("a") && atts.getValue("id") == null) {
                this.startStory();
                final String href = atts.getValue("href");
                final String clippedHref1 = href.substring(7);
                final String clippedHref2 = clippedHref1.substring(0, clippedHref1.indexOf('/'));
                this.getCurrentBuilder().id(Integer.parseInt(clippedHref2));
                this.endStory();
                this.setAfterH2(false);
            }
        } else {
            if (qName.equals("h2")) {
                this.setAfterH2(true);
            }
        }
    }
}
