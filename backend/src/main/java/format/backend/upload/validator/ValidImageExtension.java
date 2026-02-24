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
import java.util.List;
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

    private static final List<String> validImageExtensions = Arrays.stream(ImageExtension.values())
            .map(ImageExtension::getExtensionValue)
            .toList();

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        val trimmedValue = value.trim();
        val lastDotIndex = trimmedValue.lastIndexOf('.');
        val fileExtension = lastDotIndex == -1 ? trimmedValue : trimmedValue.substring(lastDotIndex + 1);
        val isValid = ImageExtension.fromExtensionValue(fileExtension).isPresent();

        if (!isValid) {
            val errorMessage = String.format(
                    "File has invalid extension '%s', only %s are allowed",
                    fileExtension, String.join(", ", validImageExtensions));
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
        }

        return isValid;
    }
}
