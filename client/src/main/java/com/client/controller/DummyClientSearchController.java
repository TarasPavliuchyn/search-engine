package com.client.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/client")
public class DummyClientSearchController {

    private final RestTemplate restTemplate;

    @Value("${search.server.url}")
    private String serverUrl;

    public DummyClientSearchController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(value = "tokens") Set<String> tokens) {

        if (!tokens.isEmpty()) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(serverUrl + "keys")
                    .queryParam("tokens", String.join(",", tokens));

            HttpEntity<?> entity = new HttpEntity<>(headers);

            HttpEntity<String> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    String.class);

            return ResponseEntity.ok(response.getBody());
        } else {
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("/documents/{key}")
    public ResponseEntity<String> getDocument(@PathVariable String key) {
        String getUrl = serverUrl + "documents/" + key;
        try {
            HttpEntity<String> response = restTemplate.getForEntity(getUrl, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (final HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    @PostMapping("/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> putDocument(@RequestBody DocumentRequestDto document) {
        HttpEntity<DocumentRequestDto> request = new HttpEntity<>(document, new HttpHeaders());

        URI locationHeader = restTemplate.postForLocation(serverUrl + "documents", request);

        return ResponseEntity.created(locationHeader).build();
    }

    public static class DocumentRequestDto {
        private String key;
        private String document;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getDocument() {
            return document;
        }

        public void setDocument(String document) {
            this.document = document;
        }
    }


}
