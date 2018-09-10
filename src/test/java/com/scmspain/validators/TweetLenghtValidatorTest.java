package com.scmspain.validators;

import javax.validation.ConstraintValidatorContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class TweetLenghtValidatorTest {

    private final static String VALID_TWEET = "Valid tweet without link";
    private final static String VALID_TWEET_WITH_LINK = "Valid tweet with link...Valid tweet with link...Valid tweet with link...Valid tweet with link.. https://www.schibsted.com/";
    private final static String INVALID_TWEET = "Invalid tweet which length is greater than 140 characters...Invalid tweet which length is greater than 140 characters...Invalid tweet which length is greater than 140 characters";

    private TweetLenghtValidator tweetLenghtValidator;
    private ConstraintValidatorContext constraintValidatorContext;

    @Before
    public void setUp() {
        tweetLenghtValidator = new TweetLenghtValidator();
        constraintValidatorContext = null;
    }

    @Test
    public void isValid_whenTweetIsValid_thenReturnTrue() {
        boolean validationResult = tweetLenghtValidator.isValid(VALID_TWEET, constraintValidatorContext);
        assertTrue("The validation of valid tweet should return true", validationResult);
    }

    @Test
    public void isValid_whenTweetWithLinkIsValid_thenReturnTrue() {
        boolean validationResult = tweetLenghtValidator.isValid(VALID_TWEET_WITH_LINK, constraintValidatorContext);
        assertTrue("The validation of valid tweet should return true", validationResult);
    }

    @Test
    public void isValid_whenTweetLengthIsGreaterThan140Characters_thenReturnFalse() {
        boolean validationResult = tweetLenghtValidator.isValid(INVALID_TWEET, constraintValidatorContext);
        assertFalse("The validation of invalid tweet should return false", validationResult);
    }

    @Test
    public void isValid_whenTweetWithLinkIsLongerThan140Characters_thenReturnFalse() {

    }

}
