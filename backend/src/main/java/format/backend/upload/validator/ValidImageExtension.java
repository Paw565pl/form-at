package format.backend.upload.validator;

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
import java.util.Arrays;
import lombok.val;

@Retention(RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Constraint(validatedBy = ImageExtensionValidator.class)
public @interface ValidImageExtension {

    String message() default "Invalid extension";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class ImageExtensionValidator implements ConstraintValidator<ValidImageExtension, String> {

    private static final String ERROR_MESSAGE = String.format(
            "File has invalid extension, only %s are allowed",
            String.join(
                    ", ",
                    Arrays.stream(ImageExtension.values())
                            .map(ImageExtension::getValue)
                            .toList()));

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        val isValid = ImageExtension.fromFilename(value).isPresent();
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ERROR_MESSAGE).addConstraintViolation();
        }

        return isValid;
    }
}
