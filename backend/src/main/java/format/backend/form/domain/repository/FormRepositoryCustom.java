package format.backend.form.domain.repository;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

interface FormRepositoryCustom {

    Page<FormListProjection> findAll(@Nullable String userId, FormListCriteria criteria, Pageable pageable);
}
