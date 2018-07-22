package me.thesquadmc.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.bson.Document;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DocumentUtils {

    public static Stream<String> documentToStringList(Document document, String key) {
        Preconditions.checkNotNull(document);
        Preconditions.checkNotNull(key);

        List<Object> list = (List<Object>) document.get(key);

        if (list == null) {
            return null;
        }

        return list.stream().filter(Objects::nonNull).map(String::valueOf);
    }

    public static Set<String> documentToStringSet(Document document, String key) {
        Stream<String> stream = documentToStringList(document, key);

        if (stream == null) {
            return Sets.newHashSet();
        }

        return stream.collect(Collectors.toSet());
    }

    public static Set<UUID> documentToUUIDSet(Document document, String key) {
        Stream<String> stream = documentToStringList(document, key);

        if (stream == null) {
            return Sets.newHashSet();
        }

        return stream.map(UUID::fromString).collect(Collectors.toSet());
    }
}
