package at.yawk.fimfiction.json;

import static at.yawk.fimfiction.json.Util.getInt;
import static at.yawk.fimfiction.json.Util.getString;
import at.yawk.fimfiction.User;
import at.yawk.fimfiction.User.UserBuilder;

import com.google.gson.JsonObject;

/**
 * {@link IJsonParser} for {@link User}s, used by {@link StoryParser} for author
 * parsing.
 * 
 * @author Yawkat
 */
public class UserParser implements IJsonParser<User> {
    @Override
    public final User parse(final JsonObject object) {
        final UserBuilder builder = User.builder();
        this.parseInto(object, builder);
        return builder.build();
    }
    
    /**
     * Parse the given {@link JsonObject} into the given {@link UserBuilder}.
     * Can be overridden for more extensive implementations.
     */
    protected void parseInto(final JsonObject parse, final UserBuilder builder) {
        builder.id(getInt(parse, "id"));
        builder.name(getString(parse, "name"));
    }
    
    @Override
    public void reset() {}
}
