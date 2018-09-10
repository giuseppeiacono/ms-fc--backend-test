package com.scmspain.controller;

import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scmspain.configuration.TestConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
public class TweetControllerTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(this.context).build();
    }

    @Test
    public void shouldReturn200WhenInsertingAValidTweet() throws Exception {
        publishTweet("Prospect", "Breaking the law")
            .andExpect(status().is(201));
    }

    @Test
    public void shouldReturn400WhenInsertingAnInvalidTweet() throws Exception {
        publishTweet("Schibsted Spain", "We are Schibsted Spain (look at our home page http://www.schibsted.es/), we own Vibbo, InfoJobs, fotocasa, coches.net and milanuncios. Welcome!")
                .andExpect(status().is(400));
    }

    @Test
    public void shouldReturnAllPublishedTweets() throws Exception {
        publishTweet("Yo", "How are you?")
                .andExpect(status().is(201));

        MvcResult getResult = mockMvc.perform(get("/tweet"))
                .andExpect(status().is(200))
                .andReturn();

        String content = getResult.getResponse().getContentAsString();
        assertThat(new ObjectMapper().readValue(content, List.class).size()).isEqualTo(1);
    }

    @Test
    public void listDiscardedTweets_whenAtLeastOneTweetsWasDiscarded_thenReturnThemOrderedByDiscardedDateDescending() throws Exception {
        // published tweets
        publishTweet("Schibsted Spain", "How are you?").andExpect(status().is(201));
        publishTweet("Schibsted Spain", "I'm fine thank you").andExpect(status().is(201));
        publishTweet("Schibsted Spain", "It's a great day!").andExpect(status().is(201));

        // discarded tweets
        discardTweet("{\"tweet\": 3}").andExpect(status().isOk());
        discardTweet("{\"tweet\": 1}").andExpect(status().isOk());

        // test
        MvcResult getResult = mockMvc.perform(get("/discarded"))
                .andExpect(status().is(200))
                .andReturn();

        // assertions
        String content = getResult.getResponse().getContentAsString();
        List discardedTweets = new ObjectMapper().readValue(content, List.class);
        assertThat(discardedTweets.size()).isEqualTo(2);
        assertThat(((LinkedHashMap) discardedTweets.get(0)).get("id")).isEqualTo(3);
        assertThat(((LinkedHashMap) discardedTweets.get(1)).get("id")).isEqualTo(1);
    }

    @Test
    public void listDiscardedTweets_whenThereAreNoTweetsDiscarded_thenReturnAnEmptyList() throws Exception {
        // published tweets
        publishTweet("Schibsted Spain", "How are you?").andExpect(status().is(201));
        publishTweet("Schibsted Spain", "I'm fine thank you").andExpect(status().is(201));

        // test
        MvcResult getResult = mockMvc.perform(get("/discarded"))
                .andExpect(status().is(200))
                .andReturn();

        // assertions
        String content = getResult.getResponse().getContentAsString();
        List discardedTweets = new ObjectMapper().readValue(content, List.class);
        assertThat(discardedTweets.size()).isEqualTo(0);
    }

    @Test
    public void discardTweet_WhenTweetIdIsEmpty_ThenReturnHttp400() throws Exception {
        publishTweet("Schibsted Spain", "How are you?").andExpect(status().is(201));

        // test
        discardTweet("").andExpect(status().is(400));
    }

    private MockHttpServletRequestBuilder newTweet(String publisher, String tweet) {
        return post("/tweet")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(format("{\"publisher\": \"%s\", \"tweet\": \"%s\"}", publisher, tweet));
    }

    private ResultActions publishTweet(final String publisher, final String tweet) throws Exception {
        return  mockMvc.perform(newTweet(publisher, tweet));
    }

    private ResultActions discardTweet(final String tweetIdJson) throws Exception {
        return mockMvc.perform(post("/discarded")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tweetIdJson));
    }

}
