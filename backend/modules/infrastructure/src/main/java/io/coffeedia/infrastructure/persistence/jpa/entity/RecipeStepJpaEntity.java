package io.coffeedia.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Builder
@Entity
@Table(name = "recipe_steps")
@Comment("레시피 단계")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class RecipeStepJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("PK")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private RecipeJpaEntity recipe;

    @Column(nullable = false)
    @Comment("순서")
    private Integer sortOrder;

    @Column(length = 2048)
    @Comment("이미지 URL")
    private String imageUrl;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Comment("레시피 설명")
    private String description;
}
