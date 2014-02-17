package at.yawk.fimfiction.core;

import at.yawk.fimfiction.data.Story;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.annotation.Nullable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Jonas Konrad (yawkat)
 */
class SearchRssParser extends SearchParser {
    private static final int NONE = 0;
    private static final int NONE_STORY = 1;
    private static final int TITLE = 2;
    private static final int DESCRIPTION = 3;
    private static final int LINK = 4;
    private static final int UPDATE_DATE = 5;

    private final SimpleDateFormat rssDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HHmmssZ", Locale.ENGLISH);

    @Nullable Story story;
    int stage = NONE;
    @Nullable StringBuilder descriptionBuilder;

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if (stage == NONE) {
            if ("item".equals(qName)) {
                story = Story.createMutable();
                stage = NONE_STORY;
            }
        } else if (stage == NONE_STORY) {
            if ("title".equals(qName)) {
                stage = TITLE;
            } else if ("description".equals(qName)) {
                stage = DESCRIPTION;
                descriptionBuilder = new StringBuilder();
            } else if ("link".equals(qName)) {
                stage = LINK;
            } else if ("pubDate".equals(qName)) {
                stage = UPDATE_DATE;
            }
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        switch (stage) {
        case NONE_STORY:
            if ("item".equals(qName)) {
                finishedStories.add(story);
                story = null;
                stage = NONE;
            }
            break;
        case DESCRIPTION:
            if ("description".equals(qName)) {
                assert story != null;
                assert descriptionBuilder != null;
                try {
                    story.set(Story.StoryKey.DESCRIPTION, FormattedStringParser.parseHtml(descriptionBuilder.toString()));
                } catch (IOException e) {
                    throw new SAXException(e);
                }
                descriptionBuilder = null;
                stage = NONE_STORY;
            }
            break;
        case NONE:
        case LINK:
            break;
        default:
            stage = NONE_STORY;
            break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String asString = new String(ch, start, length);
        switch (stage) {
        case TITLE:
            assert story != null;
            story.set(Story.StoryKey.TITLE, asString);
            stage = NONE_STORY;
            break;
        case DESCRIPTION:
            assert descriptionBuilder != null;
            descriptionBuilder.append(asString);
            break;
        case LINK:
            try {
                assert story != null;
                story.set(Story.StoryKey.URL, new URL(asString));
                String sub1 = asString.substring(asString.indexOf('y') + 2);
                String sub2 = sub1.substring(0, sub1.indexOf('/'));
                story.set(Story.StoryKey.ID, Integer.parseInt(sub2));
            } catch (MalformedURLException e) {
                throw new SAXException(e);
            }
            stage = NONE_STORY;
            break;
        case UPDATE_DATE:
            try {
                assert story != null;
                story.set(Story.StoryKey.DATE_UPDATED, rssDateFormat.parse(asString.replace(":", "")));
            } catch (ParseException e) {
                throw new SAXException(e);
            }
            stage = NONE_STORY;
            break;
        }
    }
}
