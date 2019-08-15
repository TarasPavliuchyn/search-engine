package search.engine.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import search.engine.service.DocumentSearchService;
import search.engine.service.IndexService;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class DocumentSearchServiceImp implements DocumentSearchService<String, String> {

    // visible for testing
    public ConcurrentHashMap<String, String> storage = new ConcurrentHashMap<>();

    private final IndexService<String, String> indexService;

    public DocumentSearchServiceImp(IndexService<String, String> indexService) {
        this.indexService = indexService;
    }

    @Override
    public Set<String> search(Set<String> tokens) {
        return indexService.findByAllMatch(tokens);
    }

    @Override
    public void put(String key, String document) {
        String prevValue = storage.putIfAbsent(key, document);
        if (prevValue == null) {
            indexService.indexDocument(document, key);
        } else {
            log.warn("Document overriding is not supported.");
        }
    }

    @Override
    public Optional<String> get(String key) {
        String document = storage.get(key);
        return Optional.ofNullable(document);
    }

}
