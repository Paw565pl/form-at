package format.backend.commentRating.validator;

import format.backend.core.validator.ValidObjectId;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Constraint(validatedBy = {})
@ValidObjectId(message = "CommentId must be a valid ObjectId")
public @interface ValidCommentRatingId {
    String message() default "CommentId must be a valid ObjectId";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
