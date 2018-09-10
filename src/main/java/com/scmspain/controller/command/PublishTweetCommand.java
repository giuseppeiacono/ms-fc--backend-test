package com.scmspain.controller.command;

import com.scmspain.validators.TweetLenghtConstraint;
import org.hibernate.validator.constraints.NotEmpty;

public class PublishTweetCommand {

    @NotEmpty
    private String publisher;

    @NotEmpty
    @TweetLenghtConstraint
    private String tweet;

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }
}
