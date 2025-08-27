package io.coffeedia.infrastructure.persistence.jpa.entity;

import io.coffeedia.domain.model.Bean;
import io.coffeedia.domain.vo.AccessType;
import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.BlendType;
import io.coffeedia.domain.vo.Origin;
import io.coffeedia.domain.vo.ProcessType;
import io.coffeedia.domain.vo.RoastLevel;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Builder
@Entity
@Table(name = "beans")
@Comment("원두")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class BeanJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("PK")
    private Long id;

    @Column(nullable = false, length = 80)
    @Comment("이름")
    private String name;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "country", column = @Column(name = "origin_country")),
        @AttributeOverride(name = "region", column = @Column(name = "origin_region"))
    })
    private Origin origin;

    @Column(nullable = false, length = 80)
    @Comment("로스터")
    private String roaster;

    @Column(nullable = false)
    @Comment("로스팅일자")
    private LocalDate roastDate;

    @Column(nullable = false)
    @Comment("그램")
    private int grams;

    @Column(nullable = false, length = 40)
    @Comment("로스팅레벨")
    @Enumerated(EnumType.STRING)
    private RoastLevel roastLevel;

    @Column(nullable = false, length = 40)
    @Comment("가공타입")
    @Enumerated(EnumType.STRING)
    private ProcessType processType;

    @Column(nullable = false, length = 40)
    @Comment("블렌드타입")
    @Enumerated(EnumType.STRING)
    private BlendType blendType;

    @Column(nullable = false)
    @Comment("디카페인 여부")
    private boolean isDecaf;

    @Column(length = 200)
    @Comment("메모")
    private String memo;

    @Column(nullable = false, length = 40)
    @Comment("상태")
    @Enumerated(EnumType.STRING)
    private ActiveStatus status;

    @Column(nullable = false, length = 40)
    @Comment("접근권한")
    @Enumerated(EnumType.STRING)
    private AccessType accessType;

    @Default
    @OneToMany(mappedBy = "bean", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BeanFlavorJpaEntity> beanFlavors = new ArrayList<>();

    public List<FlavorJpaEntity> getFlavors() {
        return beanFlavors.stream()
            .map(BeanFlavorJpaEntity::getFlavor)
            .toList();
    }

    public void addFlavors(final List<FlavorJpaEntity> flavors) {
        flavors.forEach(this::addFlavors);
    }

    private void addFlavors(final FlavorJpaEntity flavor) {
        beanFlavors.add(
            BeanFlavorJpaEntity.builder()
                .bean(this)
                .flavor(flavor)
                .build()
        );
    }

    public void update(final Bean bean) {
        if (bean.name() != null) {
            this.name = bean.name();
        }
        if (bean.origin() != null) {
            this.origin = bean.origin();
        }
        if (bean.roaster() != null) {
            this.roaster = bean.roaster();
        }
        if (bean.roastDate() != null) {
            this.roastDate = bean.roastDate();
        }
        if (bean.grams() >= 0) {  // primitive int는 null 체크 불가하므로 >= 0으로 체크
            this.grams = bean.grams();
        }
        if (bean.roastLevel() != null) {
            this.roastLevel = bean.roastLevel();
        }
        if (bean.processType() != null) {
            this.processType = bean.processType();
        }
        if (bean.blendType() != null) {
            this.blendType = bean.blendType();
        }
        if (bean.memo() != null) {
            this.memo = bean.memo();
        }
        if (bean.status() != null) {
            this.status = bean.status();
        }
        if (bean.accessType() != null) {
            this.accessType = bean.accessType();
        }
        this.isDecaf = bean.isDecaf();  // boolean은 primitive 타입이므로 항상 업데이트
    }
}
