package io.coffeedia.infrastructure.persistence.jpa.entity;

import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.EquipmentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@SuperBuilder
@Entity
@Table(name = "equipments")
@Comment("장비")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class EquipmentJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("PK")
    private Long id;

    @Column(nullable = false)
    @Comment("사용자 ID")
    private Long userId;

    @Column(nullable = false, length = 40)
    @Comment("타입")
    @Enumerated(EnumType.STRING)
    private EquipmentType type;

    @Column(nullable = false, length = 100)
    @Comment("이름")
    private String name;

    @Column(nullable = false, length = 80)
    @Comment("브랜드")
    private String brand;

    @Column(nullable = false, length = 40)
    @Comment("상태")
    @Enumerated(EnumType.STRING)
    private ActiveStatus status;

    @Column(length = 400)
    @Comment("설명")
    private String description;

    @Comment("구매일자")
    private LocalDate buyDate;

    @Column(length = 500)
    @Comment("구매 URL")
    private String buyUrl;
}
