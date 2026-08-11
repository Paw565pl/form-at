package format.backend.formcomment.domain.repository;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

interface FormCommentRepositoryCustom {

    Page<FormCommentListProjection> findAll(@Nullable String userId, String formId, Pageable pageable);
}
