package ru.practicum.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Target({ FIELD, METHOD, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CanNotBePendingValidator.class)
@Documented
public @interface CanNotBePending {

    String message() default "{status needs to be CONFIRMED or REJECTED";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
