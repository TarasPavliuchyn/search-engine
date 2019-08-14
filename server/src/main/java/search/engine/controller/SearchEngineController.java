package search.engine.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import search.engine.dto.DocumentRequestDto;
import search.engine.service.DocumentSearchService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class SearchEngineController {

    private final DocumentSearchService<String, String> searchService;

    public SearchEngineController(DocumentSearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/keys")
    public Set<String> searchKeys(@RequestParam(value = "tokens") Set<String> tokens) {
        log.info("Searching by {}", tokens);

        if (!tokens.isEmpty()) {
            return searchService.search(tokens);
        }

        return Collections.emptySet();
    }

    @GetMapping("/documents/{key}")
    public ResponseEntity<String> getDocument(@PathVariable String key) {
        log.info("Get document by the key [{}]", key);

        Optional<String> document = searchService.get(key);
        if (document.isPresent()) {
            return ResponseEntity.ok(document.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/documents")
    public ResponseEntity<?> putDocument(@RequestBody @Valid DocumentRequestDto document,
                                         UriComponentsBuilder b) {
        log.info("Try to add the document {}", document);
        searchService.put(document.getKey(), document.getDocument());

        UriComponents uriComponents =
                b.path("/api/v1/documents/{key}").buildAndExpand(document.getKey());

        return ResponseEntity.created(uriComponents.toUri()).build();
    }

}
