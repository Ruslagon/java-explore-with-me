package ru.practicum.validation;

import ru.practicum.model.enums.ParticipationStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CanNotBePendingValidator
        implements ConstraintValidator<CanNotBePending, ParticipationStatus> {

    @Override
    public final void initialize(final CanNotBePending annotation) {}

    @Override
    public final boolean isValid(final ParticipationStatus value,
                                 ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return !value.equals(ParticipationStatus.PENDING);
    }
}
