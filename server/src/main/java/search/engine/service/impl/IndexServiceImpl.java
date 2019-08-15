package search.engine.service.impl;

import org.springframework.stereotype.Service;
import search.engine.service.IndexService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IndexServiceImpl implements IndexService<String, String> {

    private static final String SPLITTER = "\\W+";

    private ConcurrentHashMap<String, Set<String>> indexes = new ConcurrentHashMap<>();

    @Override
    public void indexDocument(String document, String key) {
        String[] tokens = document.split(SPLITTER);
        Arrays.stream(tokens).forEach(
                token -> indexes.compute(token, (k, v) -> {
                    Set<String> keys = v;
                    if (keys == null) {
                        keys = new HashSet<>();
                    }
                    keys.add(key);
                    return keys;
                }));
    }

    @Override
    public Set<String> findByAllMatch(Set<String> tokens) {
        // if only one token then no interception
        if (tokens.size() == 1) {
            Set<String> keys = indexes.get(tokens.iterator().next());
            return keys == null ? Collections.emptySet() : keys;
        }

        List<Set<String>> allKeys = new ArrayList<>();
        for (String token : tokens) {
            Set<String> keys = indexes.get(token);
            if (keys == null) {
                return Collections.emptySet();
            } else {
                allKeys.add(keys);
            }
        }

        return intersect(allKeys);

    }

    private Set<String> intersect(List<Set<String>> nullableKeys) {
        Set<String> common = new LinkedHashSet<>();
        if (!nullableKeys.isEmpty()) {
            Iterator<Set<String>> iterator = nullableKeys.iterator();
            common.addAll(iterator.next());
            while (iterator.hasNext()) {
                common.retainAll(iterator.next());
            }
        }
        return common;
    }

}
