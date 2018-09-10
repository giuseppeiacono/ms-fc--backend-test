package com.scmspain.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TweetLenghtValidator implements ConstraintValidator<TweetLenghtConstraint, String> {

    private final static String HTTP_PREFIX = "http://";
    private final static String HTTPS_PREFIX = "https://";

    @Override
    public void initialize(final TweetLenghtConstraint constraintAnnotation) {

    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        // remove link from tweet if it includes one
        int linkStartIndex = tweetIncludesLink(value);
        String tweet = value;
        if (linkStartIndex != -1) {
            tweet = tweet.substring(0, linkStartIndex);
        }

        return tweet.length() > 0 &&
                tweet.length() < 140;
    }

    private int tweetIncludesLink(final String tweet) {
        int linkStartIndex = tweet.indexOf(HTTP_PREFIX);
        if (linkStartIndex == -1) {
            linkStartIndex = tweet.indexOf(HTTPS_PREFIX);
        }

        return linkStartIndex;
    }
}
