package format.backend.form.application.shared.validator;

import format.backend.auth.UserClaims;
import format.backend.form.application.shared.dto.FormRequestDto;
import format.backend.upload.UploadFacade;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FormImageValidator {

    private final UploadFacade uploadFacade;

    public Map<String, List<String>> validate(UserClaims userClaims, FormRequestDto requestDto, Set<String> tempKeys) {
        if (tempKeys.isEmpty()) return Map.of();

        val invalidKeys = uploadFacade.getInvalidKeys(tempKeys, userClaims);
        if (invalidKeys.isEmpty()) return Map.of();

        val errors = new LinkedHashMap<String, List<String>>();
        if (requestDto.thumbnailKey() != null && invalidKeys.contains(requestDto.thumbnailKey())) {
            errors.put(
                    "thumbnailKey",
                    List.of("Form thumbnail with key '%s' was not found in storage or is not a valid image"
                            .formatted(requestDto.thumbnailKey())));
        }

        for (var i = 0; i < requestDto.questions().size(); i++) {
            val imageKey = requestDto.questions().get(i).imageKey();
            if (imageKey != null && invalidKeys.contains(imageKey)) {
                errors.put(
                        "questions[%d].imageKey".formatted(i),
                        List.of("Question image with key '%s' was not found in storage or is not a valid image"
                                .formatted(imageKey)));
            }
        }

        return Collections.unmodifiableMap(errors);
    }
}
