package com.scmspain.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import com.scmspain.entities.Tweet;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TweetService {

    private EntityManager entityManager;
    private MetricWriter metricWriter;

    public TweetService(EntityManager entityManager, MetricWriter metricWriter) {
        this.entityManager = entityManager;
        this.metricWriter = metricWriter;
    }

    /**
     Push tweet to repository
     Parameter - publisher - creator of the Tweet
     Parameter - text - Content of the Tweet
     Result - recovered Tweet
     */
    public void publishTweet(String publisher, String text) {
        Tweet tweet = new Tweet();
        tweet.setTweet(text);
        tweet.setPublisher(publisher);

        this.metricWriter.increment(new Delta<Number>("published-tweets", 1));
        this.entityManager.persist(tweet);
    }

    /**
     Recover tweet from repository
     Parameter - id - id of the Tweet to retrieve
     Result - retrieved Tweet
     */
    public Tweet getTweet(Long id) {
        return this.entityManager.find(Tweet.class, id);
    }

    /**
     Recover tweet from repository
     Parameter - id - id of the Tweet to retrieve
     Result - retrieved Tweet
     */
    public List<Tweet> listAllTweets() {
        List<Tweet> result = new ArrayList<>();
        this.metricWriter.increment(new Delta<Number>("times-queried-tweets", 1));
        TypedQuery<Long> query = this.entityManager.createQuery("SELECT id FROM Tweet AS tweetId WHERE pre2015MigrationStatus<>99 AND isDiscarded = FALSE  ORDER BY id DESC", Long.class);
        query.getResultList().forEach(tweetId -> result.add(getTweet(tweetId)));
        return result;
    }

    /**
     * Discard the tweet with id {@code tweetId}.
     * It means that it will not be shown in the published tweet list
     *
     * @param tweetId the id of tweet
     */
    public void discardTweet(final Long tweetId) {
        Query query = getSession().createQuery("update Tweet t set t.isDiscarded = :isDiscarded WHERE t.id = :tweetId");
        query.setParameter("isDiscarded", Boolean.TRUE);
        query.setParameter("tweetId", tweetId);
        query.executeUpdate();
    }

    /**
     * Recover all discarded tweets and order them by the date it was discarded on in descending order
     *
     * @return discarded tweets
     */
    public List<Tweet> listDiscardedTweets() {
        List<Tweet> result = new ArrayList<>();
        TypedQuery<Long> query = this.entityManager.createQuery("SELECT id FROM Tweet AS tweetId WHERE pre2015MigrationStatus<>99 AND isDiscarded = TRUE  ORDER BY id DESC", Long.class);
        query.getResultList().forEach(tweetId -> result.add(getTweet(tweetId)));
        return result;
    }

    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }
}
