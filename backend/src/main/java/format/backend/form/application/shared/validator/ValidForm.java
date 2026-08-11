package format.backend.form.application.shared.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import format.backend.form.application.shared.dto.FormRequestDto;
import format.backend.form.application.shared.dto.QuestionRequestDto;
import format.backend.form.domain.entity.FormStatus;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import lombok.val;

@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE})
@Constraint(validatedBy = FormValidator.class)
public @interface ValidForm {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

final class FormValidator implements ConstraintValidator<ValidForm, FormRequestDto> {

    @Override
    public boolean isValid(FormRequestDto value, ConstraintValidatorContext context) {
        if (value == null) return true;
        context.disableDefaultConstraintViolation();

        val isPasswordValid = isPasswordValid(value, context);
        val isRequiredQuestionsCountValid = isRequiredQuestionsCountValid(value, context);

        return isPasswordValid && isRequiredQuestionsCountValid;
    }

    private static boolean isPasswordValid(FormRequestDto value, ConstraintValidatorContext context) {
        if (value.status() != FormStatus.PRIVATE) return true;

        val isPasswordInvalid = value.password() == null || value.password().isBlank();
        if (isPasswordInvalid) {
            context.buildConstraintViolationWithTemplate(
                            "Password cannot be blank for form with status '%s'".formatted(FormStatus.PRIVATE))
                    .addPropertyNode("password")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }

    private static boolean isRequiredQuestionsCountValid(FormRequestDto value, ConstraintValidatorContext context) {
        if (value.questions() == null) return true;

        val requiredQuestionsCount = value.questions().stream()
                .filter(QuestionRequestDto::isRequired)
                .count();
        if (requiredQuestionsCount < 1) {
            context.buildConstraintViolationWithTemplate("Form must have at least one required question")
                    .addPropertyNode("questions")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}
