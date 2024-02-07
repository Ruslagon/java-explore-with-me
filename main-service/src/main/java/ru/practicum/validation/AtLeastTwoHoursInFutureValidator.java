package ru.practicum.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class AtLeastTwoHoursInFutureValidator
        implements ConstraintValidator<AtLeastTwoHoursInFuture, LocalDateTime> {

    @Override
    public final void initialize(final AtLeastTwoHoursInFuture annotation) {}

    @Override
    public final boolean isValid(final LocalDateTime value,
                                 ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return LocalDateTime.now().plusHours(2).isBefore(value);
    }
}