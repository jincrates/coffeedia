package io.coffeedia.infrastructure.persistence.jpa.repository;

import io.coffeedia.domain.model.RecipeSummary;
import io.coffeedia.infrastructure.persistence.jpa.entity.RecipeJpaEntity;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecipeJpaRepository extends JpaRepository<RecipeJpaEntity, Long> {

    @Query("""
        SELECT new io.coffeedia.domain.model.RecipeSummary(
            r.id,
            r.userId,
            r.category,
            r.title,
            r.thumbnailUrl,
            r.createdAt,
            r.updatedAt
        )
        FROM RecipeJpaEntity r
        WHERE r.status = 'ACTIVE'
        """)
    List<RecipeSummary> findAllSummaries(Pageable pageable);
}
