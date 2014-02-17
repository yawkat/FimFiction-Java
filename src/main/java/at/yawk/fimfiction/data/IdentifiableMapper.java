package at.yawk.fimfiction.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Jonas Konrad (yawkat)
 */
class IdentifiableMapper {
    private static final Map<Class<? extends Identifiable>, Map<String, Identifiable>> mappings = Maps.newHashMap();

    static void addMapping(@Nonnull Class<? extends Identifiable> type, @Nonnull Identifiable... values) {
        Map<String, Identifiable> mapping = Maps.newHashMap();
        for (Identifiable identifiable : values) { mapping.put(identifiable.getId(), identifiable); }
        mappings.put(type, mapping);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    static <I extends Identifiable> I findIdentifiable(Class<I> type, String id) {
        Preconditions.checkNotNull(id);
        return (I) mappings.get(type).get(id);
    }
}
