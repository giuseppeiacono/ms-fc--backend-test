package com.scmspain.services;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.scmspain.entities.Tweet;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TweetServiceTest {
    private EntityManager entityManager;
    private MetricWriter metricWriter;
    private TweetService tweetService;

    @Before
    public void setUp() throws Exception {
        this.entityManager = mock(EntityManager.class);
        this.metricWriter = mock(MetricWriter.class);

        this.tweetService = new TweetService(entityManager, metricWriter);
    }

    @Test
    public void shouldInsertANewTweet() throws Exception {
        tweetService.publishTweet("Guybrush Threepwood", "I am Guybrush Threepwood, mighty pirate.");

        verify(entityManager).persist(any(Tweet.class));
    }

    @Test
    public void listDiscardedTweets_WhenDiscardedTweetsAreRequired_ThenCompleteListIsReturned() {
        TypedQuery query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT id FROM Tweet AS tweetId WHERE pre2015MigrationStatus<>99 AND isDiscarded = TRUE  ORDER BY id DESC", Long.class)).thenReturn(query);

        // test
        tweetService.listDiscardedTweets();

        // assertions
        verify(entityManager).createQuery("SELECT id FROM Tweet AS tweetId WHERE pre2015MigrationStatus<>99 AND isDiscarded = TRUE  ORDER BY id DESC", Long.class);
    }
}
