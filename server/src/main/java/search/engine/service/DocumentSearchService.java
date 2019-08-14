package search.engine.service;

import java.util.Optional;
import java.util.Set;

public interface DocumentSearchService<K, V> {

    /**
     * Search by tokens to return keys of all documents that contain all tokens in the set
     *
     * @return - keys of documents.
     */
    Set<K> search(Set<V> tokens);

    /**
     * Put documents into the search engine by key.
     */
    void put(K key, V value);

    /**
     * Get document by key.
     */
    Optional<V> get(K key);

}