package io.coffeedia.infrastructure.persistence.jpa;

import io.coffeedia.application.port.repository.RecipeRepositoryPort;
import io.coffeedia.domain.model.Recipe;
import io.coffeedia.domain.model.RecipeSummary;
import io.coffeedia.domain.vo.PageSize;
import io.coffeedia.domain.vo.SortType;
import io.coffeedia.infrastructure.persistence.jpa.entity.RecipeJpaEntity;
import io.coffeedia.infrastructure.persistence.jpa.entity.TagJpaEntity;
import io.coffeedia.infrastructure.persistence.jpa.mapper.RecipeJpaMapper;
import io.coffeedia.infrastructure.persistence.jpa.repository.RecipeJpaRepository;
import io.coffeedia.infrastructure.persistence.jpa.repository.TagJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class RecipeRepositoryAdapter implements RecipeRepositoryPort {

    private final RecipeJpaRepository recipeRepository;
    private final TagJpaRepository tagRepository;

    @Override
    public Recipe save(final Recipe recipe) {
        List<TagJpaEntity> tags = findOrCreateTagsByNames(recipe.tags());
        RecipeJpaEntity entity = RecipeJpaMapper.toEntity(recipe, tags);
        RecipeJpaEntity saved = recipeRepository.save(entity);
        return RecipeJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Recipe> findById(final Long id) {
        return recipeRepository.findById(id)
            .map(RecipeJpaMapper::toDomain);
    }

    @Override
    public List<RecipeSummary> findAll(final PageSize pageSize, final List<SortType> sorts) {
        Pageable pageable = PageRequest.of(
            pageSize.page(),
            pageSize.size() + 1,  // 다음 페이지가 있는지 확인하기 위해 +1
            toSort(sorts)
        );
        return recipeRepository.findAllSummaries(pageable);
    }

    @Override
    public void deleteById(final Long id) {
        recipeRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        recipeRepository.deleteAll();
    }

    private List<TagJpaEntity> findOrCreateTagsByNames(final List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return List.of();
        }

        List<TagJpaEntity> existingTags = tagRepository.findAllByNameIn(tagNames);

        if (tagNames.size() == existingTags.size()) {
            return existingTags;
        }

        // 기존에 있는 태그 이름들 추출
        Set<String> existingTagNames = existingTags.stream()
            .map(TagJpaEntity::getName)
            .collect(Collectors.toSet());

        // 없는 태그들 찾아서 새로 생성
        List<TagJpaEntity> newTags = tagNames.stream()
            .filter(tagName -> !existingTagNames.contains(tagName))
            .map(tagName -> TagJpaEntity.builder()
                .name(tagName)
                .build())
            .toList();

        // 새로운 태그들 저장
        List<TagJpaEntity> savedNewTags = tagRepository.saveAll(newTags);

        // 불변 리스트로 반환 (기존 리스트를 수정하지 않음)
        return Stream.concat(existingTags.stream(), savedNewTags.stream())
            .toList();
    }

    private Sort toSort(final List<SortType> sorts) {
        List<Sort.Order> orders = sorts.stream()
            .map(sort -> new Sort.Order(
                Sort.Direction.fromString(sort.getDirection()), sort.getField())
            )
            .toList();
        return Sort.by(orders);
    }
}
