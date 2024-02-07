package ru.practicum.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

@Target({ FIELD, METHOD, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastTwoHoursInFutureValidator.class)
@Documented
public @interface AtLeastTwoHoursInFuture {
    String message() default "{dateTime needs to be at least two hours in Future}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}