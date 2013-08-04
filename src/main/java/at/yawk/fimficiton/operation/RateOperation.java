package at.yawk.fimficiton.operation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import at.yawk.fimficiton.FimFiction;
import at.yawk.fimficiton.Story;
import at.yawk.fimficiton.Story.Rating;

/**
 * Operation for rating a story. Returns the given story with updated like &
 * dislike status and updated like & dislike count.
 * 
 * @author Yawkat
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class RateOperation extends AbstractRequest<Story> {
    Story story;
    Rating rating;
    
    public RateOperation(final Story story, final Rating rating) {
        if (story.getRatingToken() == null) {
            throw new IllegalArgumentException("RateOperation requires a rating token.");
        }
        if (rating == Rating.NONE) {
            throw new IllegalArgumentException("Cannot set rating to NONE.");
        }
        
        this.story = story;
        this.rating = rating;
    }
    
    @Override
    protected Story request(final FimFiction session) throws Exception {
        final RatingParser parser = new RatingParser();
        
        final URL rateUrl = new URL("http://fimfiction.net/rate.php");
        final URLConnection connection = rateUrl.openConnection();
        Util.preparePost(connection);
        connection.setRequestProperty("Cookie", Util.getCookies(session));
        connection.connect();
        @Cleanup final Writer post = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        post.write("story=");
        post.write(Integer.toString(this.getStory().getId()));
        post.write("&rating=");
        post.write(this.getRating() == Rating.LIKED ? "100" : "0");
        post.write("&ip=");
        post.write(this.getStory().getRatingToken());
        @Cleanup final Reader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        final XMLReader xmlReader = new Parser();
        xmlReader.setContentHandler(parser);
        xmlReader.parse(new InputSource(reader));
        
        return this.story.withLikeCount(parser.likeCount).withDislikeCount(parser.dislikeCount).withRating(parser.isLiked() ? Rating.LIKED : parser.isDisliked() ? Rating.DISLIKED : Rating.NONE);
    }
}

@Getter
@NoArgsConstructor
class RatingParser extends DefaultHandler {
    int likeCount;
    int dislikeCount;
    boolean liked;
    boolean disliked;
    
    @Getter(AccessLevel.NONE) int stage;
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) {
        if (qName.equals("likes")) {
            this.stage = 1;
        } else if (qName.equals("dislikes")) {
            this.stage = 2;
        } else if (qName.equals("liked")) {
            this.stage = 3;
        } else if (qName.equals("disliked")) {
            this.stage = 4;
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (this.stage != 0) {
            final int val = Integer.parseInt(new String(ch, start, length));
            switch (this.stage) {
            case 1:
                this.likeCount = val;
                break;
            case 2:
                this.dislikeCount = val;
                break;
            case 3:
                this.liked = val == 1;
                break;
            case 4:
                this.disliked = val == 1;
                break;
            }
            this.stage = 0;
        }
    }
}
