package io.coffeedia.infrastructure.persistence.jpa.entity;

import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.CategoryType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@SuperBuilder
@Entity
@Table(name = "recipes")
@Comment("레시피")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class RecipeJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("PK")
    private Long id;

    @Column(nullable = false)
    @Comment("사용자 ID")
    private Long userId;

    @Column(nullable = false, length = 20)
    @Comment("카테고리")
    @Enumerated(EnumType.STRING)
    private CategoryType category;

    @Column(nullable = false, length = 100)
    @Comment("제목")
    private String title;

    @Column(length = 500)
    @Comment("썸네일 url")
    private String thumbnailUrl;

    @Column(length = 1000)
    @Comment("상세 설명")
    private String description;

    @Column(nullable = false)
    @Comment("인분")
    private Integer serving;

    @Default
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeTagJpaEntity> recipeTags = new ArrayList<>();

    @Default
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IngredientJpaEntity> ingredients = new ArrayList<>();

    @Default
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<RecipeStepJpaEntity> steps = new ArrayList<>();

    @Column(length = 200)
    @Comment("팁/주의사항")
    private String tips;

    @Column(nullable = false, length = 20)
    @Comment("상태")
    @Enumerated(EnumType.STRING)
    private ActiveStatus status;
}
