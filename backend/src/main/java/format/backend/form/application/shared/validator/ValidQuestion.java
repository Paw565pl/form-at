package format.backend.form.application.shared.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import format.backend.form.application.shared.dto.AnswerRequestDto;
import format.backend.form.application.shared.dto.QuestionRequestDto;
import format.backend.form.domain.entity.QuestionType;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import lombok.val;

@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE})
@Constraint(validatedBy = QuestionValidator.class)
public @interface ValidQuestion {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

final class QuestionValidator implements ConstraintValidator<ValidQuestion, QuestionRequestDto> {

    @Override
    public boolean isValid(QuestionRequestDto value, ConstraintValidatorContext context) {
        if (value == null) return true;
        context.disableDefaultConstraintViolation();

        return hasOneCorrectAnswerIfTypeIsNotOpen(value, context);
    }

    private static boolean hasOneCorrectAnswerIfTypeIsNotOpen(
            QuestionRequestDto value, ConstraintValidatorContext context) {
        if (value.type() == QuestionType.OPEN) return true;

        val correctAnswersCount =
                value.answers().stream().filter(AnswerRequestDto::isCorrect).count();
        if (correctAnswersCount < 1) {
            context.buildConstraintViolationWithTemplate("Question must have at least one correct answer")
                    .addPropertyNode("answers")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}
