package at.yawk.fimfiction.core;

import at.yawk.fimfiction.data.Story;
import com.google.common.collect.Lists;
import java.util.List;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Jonas Konrad (yawkat)
 */
abstract class SearchParser extends DefaultHandler {
    final List<Story> finishedStories = Lists.newArrayList();
}
