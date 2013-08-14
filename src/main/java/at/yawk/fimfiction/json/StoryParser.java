package at.yawk.fimfiction.json;

import static at.yawk.fimfiction.json.Util.getBoolean;
import static at.yawk.fimfiction.json.Util.getInt;
import static at.yawk.fimfiction.json.Util.getLong;
import static at.yawk.fimfiction.json.Util.getString;
import static at.yawk.fimfiction.json.Util.getUrl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import at.yawk.fimfiction.Chapter;
import at.yawk.fimfiction.Story;
import at.yawk.fimfiction.Story.Category;
import at.yawk.fimfiction.Story.ContentRating;
import at.yawk.fimfiction.Story.Status;
import at.yawk.fimfiction.Story.StoryBuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * {@link IJsonParser} for {@link Story} instances.
 * 
 * @author Yawkat
 */
public class StoryParser implements IJsonParser<Story> {
    @Override
    public final Story parse(final JsonObject object) {
        final StoryBuilder builder = Story.builder();
        this.parseInto(object, builder);
        return builder.build();
    }
    
    /**
     * Parse the given {@link JsonObject} into the given {@link StoryBuilder}.
     * Can be overridden for more extensive implementations.
     */
    protected void parseInto(final JsonObject parse, final StoryBuilder builder) {
        builder.id(getInt(parse, "id"));
        builder.title(getString(parse, "title"));
        builder.description(getString(parse, "description"));
        builder.descriptionShort(getString(parse, "short_description"));
        builder.modificationDate(getLong(parse, "date_modified") * 1000L);
        builder.imageUrl(getUrl(parse, "full_image", "http:"));
        builder.thumbnailUrl(getUrl(parse, "image", "http:"));
        builder.totalViewCount(getInt(parse, "views"));
        builder.wordCount(getInt(parse, "words"));
        builder.commentCount(getInt(parse, "comments"));
        builder.status(StoryParser.parseStoryStatus(getString(parse, "status")));
        builder.contentRating(parseContentRating(getInt(parse, "content_rating", -1)));
        builder.likeCount(getInt(parse, "likes"));
        builder.dislikeCount(getInt(parse, "dislikes"));
        if (parse.has("categories")) {
            final JsonElement categories = parse.get("categories");
            if (categories.isJsonObject()) {
                builder.categories(new CategoryParser().parse(categories.getAsJsonObject()));
            } else if (categories.isJsonArray()) {
                builder.categories(new CategoryParser().parse(categories.getAsJsonArray()));
            }
        }
        if (parse.has("author")) {
            builder.author(new UserParser().parse(parse.getAsJsonObject("author")));
        }
        if (parse.has("chapters")) {
            final JsonArray chapters = parse.getAsJsonArray("chapters");
            final List<Chapter> chapterList = new ArrayList<Chapter>(chapters.size());
            final ChapterParser parser = new ChapterParser();
            for (final JsonElement chapter : chapters) {
                chapterList.add(parser.parse(chapter.getAsJsonObject()));
                parser.reset();
            }
            builder.chapters(Collections.unmodifiableList(chapterList));
        }
    }
    
    /**
     * Parses the given "display string" as used by FimFiction into a
     * {@link Status} choice or <code>null</code> if the given {@link String} is
     * <code>null</code> or it could not be parsed.
     */
    private static Status parseStoryStatus(final String asString) {
        if (asString == null) {
            return null;
        } else if (asString.equals("Complete")) {
            return Status.COMPLETE;
        } else if (asString.equals("Incomplete")) {
            return Status.INCOMPLETE;
        } else if (asString.equals("On Hiatus")) {
            return Status.ON_HIATUS;
        } else if (asString.equals("Cancelled")) {
            return Status.CANCELLED;
        } else {
            return null;
        }
    }
    
    /**
     * Parses the given rating ID as used by FimFiction into a
     * {@link ContentRating} choice. Returns <code>null</code> if none could be
     * matched.
     */
    private static ContentRating parseContentRating(final int ratingId) {
        switch (ratingId) {
        case 0:
            return ContentRating.EVERYONE;
        case 1:
            return ContentRating.TEEN;
        case 2:
            return ContentRating.MATURE;
        default:
            return null;
        }
    }
    
    @Override
    public void reset() {}
}

class CategoryParser implements IJsonParser<Set<Category>> {
    @Override
    public Set<Category> parse(final JsonObject object) {
        final Set<Category> result = EnumSet.noneOf(Category.class);
        if (getBoolean(object, "Romance")) {
            result.add(Category.ROMANCE);
        }
        if (getBoolean(object, "Tragedy")) {
            result.add(Category.TRAGEDY);
        }
        if (getBoolean(object, "Sad")) {
            result.add(Category.SAD);
        }
        if (getBoolean(object, "Dark")) {
            result.add(Category.DARK);
        }
        if (getBoolean(object, "Comedy")) {
            result.add(Category.COMEDY);
        }
        if (getBoolean(object, "Random")) {
            result.add(Category.RANDOM);
        }
        if (getBoolean(object, "Crossover")) {
            result.add(Category.CROSSOVER);
        }
        if (getBoolean(object, "Adventure")) {
            result.add(Category.ADVENTURE);
        }
        if (getBoolean(object, "Slice of Life")) {
            result.add(Category.SLICE_OF_LIFE);
        }
        if (getBoolean(object, "Alternate Universe")) {
            result.add(Category.ALTERNATE_UNIVERSE);
        }
        if (getBoolean(object, "Human")) {
            result.add(Category.HUMAN);
        }
        if (getBoolean(object, "Anthro")) {
            result.add(Category.ANTHRO);
        }
        return Collections.unmodifiableSet(result);
    }
    
    /**
     * Parses the given {@link JsonArray} to a {@link Set} of categories.
     */
    public Set<Category> parse(final JsonArray object) {
        final Set<Category> result = EnumSet.noneOf(Category.class);
        for (final JsonElement elem : object) {
            final String categoryName = elem.getAsString();
            if (categoryName.equals("romance")) {
                result.add(Category.ROMANCE);
            } else if (categoryName.equals("tragedy")) {
                result.add(Category.TRAGEDY);
            } else if (categoryName.equals("sad")) {
                result.add(Category.SAD);
            } else if (categoryName.equals("dark")) {
                result.add(Category.DARK);
            } else if (categoryName.equals("comedy")) {
                result.add(Category.COMEDY);
            } else if (categoryName.equals("random")) {
                result.add(Category.RANDOM);
            } else if (categoryName.equals("crossover")) {
                result.add(Category.CROSSOVER);
            } else if (categoryName.equals("adventure")) {
                result.add(Category.ADVENTURE);
            } else if (categoryName.equals("slice_of_life")) {
                result.add(Category.SLICE_OF_LIFE);
            } else if (categoryName.equals("alternate_universe")) {
                result.add(Category.ALTERNATE_UNIVERSE);
            } else if (categoryName.equals("human")) {
                result.add(Category.HUMAN);
            } else if (categoryName.equals("anthro")) {
                result.add(Category.ANTHRO);
            }
        }
        return Collections.unmodifiableSet(result);
    }
    
    @Override
    public void reset() {}
}
