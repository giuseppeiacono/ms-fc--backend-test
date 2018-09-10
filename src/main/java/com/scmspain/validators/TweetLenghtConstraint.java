package com.scmspain.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

@Documented
@Constraint(validatedBy = TweetLenghtValidator.class)
@Target( { METHOD, FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TweetLenghtConstraint {

    String message() default "Tweet must not be greater than 140 characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
