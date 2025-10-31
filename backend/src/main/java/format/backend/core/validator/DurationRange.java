package format.backend.core.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.Duration;

@Retention(RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Constraint(validatedBy = DurationRangeValidator.class)
public @interface DurationRange {

    String message() default "Duration value does not meet given range";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return minimum duration value in ISO 8601 format, e.g. "PT1S" for 1 second
     */
    String min() default "PT0S";

    /**
     * @return maximum duration value in ISO 8601 format, e.g. "PT1S" for 1 second
     */
    String max() default "P100Y";
}

class DurationRangeValidator implements ConstraintValidator<DurationRange, Duration> {

    private Duration minDuration;
    private Duration maxDuration;

    @Override
    public void initialize(DurationRange constraintAnnotation) {
        minDuration = Duration.parse(constraintAnnotation.min());
        maxDuration = Duration.parse(constraintAnnotation.max());
    }

    @Override
    public boolean isValid(Duration value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return value.compareTo(minDuration) >= 0 && value.compareTo(maxDuration) <= 0;
    }
}
