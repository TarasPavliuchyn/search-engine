package search.engine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import search.engine.dto.DocumentRequestDto;
import search.engine.service.DocumentSearchService;
import search.engine.service.IndexService;

import java.util.Collections;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SearchEngineControllerTest {

    private static final String INSTAGRAM = "www.instagram.com";
    private static final String INSTAGRAM_TAGS = "likes friends pictures stories cats girls celebrities health";
    private static final String FACEBOOK = "www.facebook.com";
    private static final String FACEBOOK_TAGS = "likes friends pictures stories news politics games people celebrities health";
    private static final String NY_TIMES = "www.nytimes.com";
    private static final String BBC_TAGS = "news people politics war economics money oil climate celebrities health";
    private static final String DOC_URL = "/api/v1/documents/";
    private static final String KEY_URL = "/api/v1/keys/";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DocumentSearchService<String, String> documentSearchService;

    @Autowired
    private IndexService<String, String> indexService;

    @Before
    public void setUp() {
        documentSearchService.put(INSTAGRAM, INSTAGRAM_TAGS);
        documentSearchService.put(FACEBOOK, FACEBOOK_TAGS);
        documentSearchService.put(NY_TIMES, BBC_TAGS);
    }

    @Test
    public void testGetDocument() throws Exception {
        mvc.perform(get(DOC_URL + INSTAGRAM)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTAGRAM_TAGS));
    }

    @Test
    public void testGetDocument_when_notFound() throws Exception {
        mvc.perform(get(DOC_URL + "not_exists")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPutDocument() throws Exception {
        String key = "new-url.net";
        String tags = "tag1 tag2 tag3";
        DocumentRequestDto request = new DocumentRequestDto();
        request.setKey(key);
        request.setDocument(tags);

        mvc.perform(post(DOC_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated());

        assertEquals(tags, documentSearchService.get(key).orElse("fail"));
        assertEquals(Collections.singleton(key), indexService.findByAllMatch(convertStrToTagSet(tags)));

    }

    @Test
    public void testSearchKeys() throws Exception {
        mvc.perform(get(KEY_URL)
                .param("tokens", "celebrities", "health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$", hasItem(INSTAGRAM)))
                .andExpect(jsonPath("$", hasItem(FACEBOOK)))
                .andExpect(jsonPath("$", hasItem(NY_TIMES)));
    }

    @Test
    public void testSearchKeys_when_noResults() throws Exception {
        mvc.perform(get(KEY_URL)
                .param("tokens", "likes", "friends", "economics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private HashSet<String> convertStrToTagSet(String tags) {
        return new HashSet<>(asList(tags.split("\\W+")));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
