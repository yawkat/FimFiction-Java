package at.yawk.fimfiction.json;

import com.google.gson.JsonObject;

/**
 * Generic base interface for a parser that can turn a given {@link JsonObject}
 * into an object.
 * 
 * @author Yawkat
 */
public interface IJsonParser<R> {
    /**
     * Parse the given {@link JsonObject} and return the parsed result. Can only
     * performed once before {@link #reset()} must be called.
     */
    R parse(final JsonObject object);
    
    /**
     * Reset this parser and make it available for another call of
     * {@link #parse(JsonObject)}.
     */
    void reset();
}
