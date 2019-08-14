package search.engine.service;

import java.util.Set;

public interface IndexService<I, V> {

    /**
     * The method creates inverted index of document and key.
     *
     * @param document - set of tokens
     * @param key      - key to the document
     */
    void indexDocument(V document, V key);

    /**
     * Finds all matching values for {@code indexes}
     *
     * @return matching values
     */
    Set<V> findByAllMatch(Set<I> indexes);
}
