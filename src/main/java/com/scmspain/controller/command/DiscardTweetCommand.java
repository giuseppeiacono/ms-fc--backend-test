package com.scmspain.controller.command;

import javax.validation.constraints.NotNull;

public class DiscardTweetCommand {

    @NotNull
    private Long tweet;

    public Long getTweet() {
        return tweet;
    }

    public void setTweet(final Long tweet) {
        this.tweet = tweet;
    }
}
