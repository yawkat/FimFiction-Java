package at.yawk.fimfiction.html;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import at.yawk.fimfiction.Story;
import at.yawk.fimfiction.Story.StoryBuilder;

/**
 * Abstract base class for parsing a FimFiction search page. Subclasses can
 * override methods of the {@link ContentHandler} interface to parse story data
 * and store it into {@link #getCurrentBuilder()}. Start and end of story data
 * in the document is to be marked with {@link #startStory()} and
 * {@link #endStory()}.
 * 
 * @author Yawkat
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class AbstractSearchParser extends DefaultHandler implements IHtmlParser<List<Story>> {
    @Getter(AccessLevel.PRIVATE) final List<Story> readStories = new ArrayList<Story>();
    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PRIVATE) StoryBuilder currentBuilder;
    
    /**
     * Parse the HTML page from the given reader into a list of stories.
     */
    @Override
    public final List<Story> parse(final Reader reader) throws IOException, SAXException {
        final XMLReader xmlReader = new Parser();
        xmlReader.setContentHandler(this);
        xmlReader.parse(new InputSource(reader));
        
        return this.getReadStories();
    }
    
    /**
     * Clear the story cache and current build status.
     */
    @Override
    public void reset() {
        this.readStories.clear();
        this.setCurrentBuilder(null);
    }
    
    /**
     * Start building a new story.
     */
    protected final void startStory() {
        if (this.getCurrentBuilder() != null) {
            throw new IllegalStateException("Story builder already open");
        }
        this.setCurrentBuilder(Story.builder());
    }
    
    /**
     * Stop building the current story.
     */
    protected final void endStory() {
        if (this.getCurrentBuilder() == null) {
            throw new IllegalStateException("No story builder open");
        }
        this.readStories.add(this.getCurrentBuilder().build());
        this.setCurrentBuilder(null);
    }
    
    /**
     * Do nothing. Subclasses of this parser class should not be dependend on
     * this but should use the {@link #reset()} method and constructor instead.
     */
    @Override
    public final void startDocument() throws SAXException {}
    
    /**
     * Clean up the current building story and store it in the story cache. No
     * more build operations may be performed after this point.
     */
    @Override
    public final void endDocument() throws SAXException {}
    
    @Override
    public final void setDocumentLocator(final Locator locator) {}
}
