package io.coffeedia.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
@Table(name = "ingredients")
@Comment("재료")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class IngredientJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("PK")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private RecipeJpaEntity recipe;

    @Column(nullable = false, length = 100)
    @Comment("이름")
    private String name;

    @Column(nullable = false)
    @Comment("양")
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    @Comment("단위")
    private String unit;

    @Column(length = 2048)
    @Comment("구매 URL")
    private String buyUrl;
}
