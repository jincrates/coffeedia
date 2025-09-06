package io.coffeedia.bootstrap.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.coffeedia.IntegrationSupportTest;
import io.coffeedia.application.usecase.dto.BeanResponse;
import io.coffeedia.application.usecase.dto.CreateBeanCommand;
import io.coffeedia.application.usecase.dto.DeleteBeanResponse;
import io.coffeedia.application.usecase.dto.UpdateBeanCommand;
import io.coffeedia.bootstrap.api.controller.dto.BaseResponse;
import io.coffeedia.bootstrap.api.controller.dto.PageResponse;
import io.coffeedia.domain.model.Bean;
import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.BlendType;
import io.coffeedia.domain.vo.Origin;
import io.coffeedia.domain.vo.ProcessType;
import io.coffeedia.domain.vo.RoastLevel;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;

@Tag("integration")
class BeanControllerTest extends IntegrationSupportTest {

    @BeforeEach
    void setUp() {
        cleanUpBeans();
    }

    @Nested
    @DisplayName("원두 등록")
    class CreateBeanTest {

        @Test
        @DisplayName("필수 정보만으로도 원두가 정상적으로 등록된다")
        void createBeanWithRequiredFieldsOnly() {
            // given
            CreateBeanCommand command = CreateBeanCommand.builder()
                .name("에티오피아 예가체프")
                .origin(new Origin("에티오피아", "예가체프"))
                .roaster("커피로스터")
                .roastDate(LocalDate.now().minusDays(3))
                .grams(250)
                .flavorIds(List.of(1L, 2L))
                .build();

            // when
            webTestClient.post()
                .uri("/api/beans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<BaseResponse<BeanResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.message()).isBlank();
                    assertThat(response.data()).isNotNull();

                    BeanResponse data = response.data();
                    assertAll(
                        () -> assertThat(data.name()).isEqualTo(command.name()),
                        () -> assertThat(data.origin().country()).isEqualTo(
                            command.origin().country()),
                        () -> assertThat(data.origin().region()).isEqualTo(
                            command.origin().region()),
                        () -> assertThat(data.roaster()).isEqualTo(command.roaster()),
                        () -> assertThat(data.roastDate()).isEqualTo(command.roastDate()),
                        () -> assertThat(data.grams()).isEqualTo(command.grams()),
                        () -> assertThat(data.flavors()).isNotEmpty(),
                        () -> assertThat(data.roastLevel()).isEqualTo(RoastLevel.UNKNOWN),
                        () -> assertThat(data.processType()).isEqualTo(ProcessType.UNKNOWN),
                        () -> assertThat(data.blendType()).isEqualTo(BlendType.UNKNOWN),
                        () -> assertThat(data.isDecaf()).isFalse(),
                        () -> assertThat(data.status()).isEqualTo(ActiveStatus.ACTIVE),
                        () -> assertThat(data.memo()).isNull(),
                        () -> assertThat(data.createdAt()).isNotNull(),
                        () -> assertThat(data.updatedAt()).isNotNull()
                    );
                });
        }

        @Test
        @DisplayName("모든 원두 정보를 입력하면 완전한 원두 프로필이 생성된다")
        void createBeanWithAllFields() {
            // given
            CreateBeanCommand command = CreateBeanCommand.builder()
                .name("콜롬비아 수프리모")
                .origin(new Origin("콜롬비아", "나리뇨"))
                .roaster("스페셜티 로스터")
                .roastDate(LocalDate.now().minusDays(1))
                .grams(200)
                .flavorIds(List.of(1L, 2L))
                .roastLevel(RoastLevel.MEDIUM)
                .processType(ProcessType.WASHED)
                .blendType(BlendType.SINGLE_ORIGIN)
                .isDecaf(false)
                .memo("산미가 좋은 원두")
                .status(ActiveStatus.ACTIVE)
                .build();

            // when
            webTestClient.post()
                .uri("/api/beans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<BaseResponse<BeanResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.message()).isBlank();
                    assertThat(response.data()).isNotNull();

                    BeanResponse data = response.data();
                    assertAll(
                        () -> assertThat(data.name()).isEqualTo(command.name()),
                        () -> assertThat(data.origin().country()).isEqualTo(
                            command.origin().country()),
                        () -> assertThat(data.origin().region()).isEqualTo(
                            command.origin().region()),
                        () -> assertThat(data.roaster()).isEqualTo(command.roaster()),
                        () -> assertThat(data.roastDate()).isEqualTo(command.roastDate()),
                        () -> assertThat(data.grams()).isEqualTo(command.grams()),
                        () -> assertThat(data.flavors()).isNotEmpty(),
                        () -> assertThat(data.roastLevel()).isEqualTo(command.roastLevel()),
                        () -> assertThat(data.processType()).isEqualTo(command.processType()),
                        () -> assertThat(data.blendType()).isEqualTo(command.blendType()),
                        () -> assertThat(data.isDecaf()).isEqualTo(command.isDecaf()),
                        () -> assertThat(data.status()).isEqualTo(command.status()),
                        () -> assertThat(data.memo()).isEqualTo(command.memo()),
                        () -> assertThat(data.createdAt()).isNotNull(),
                        () -> assertThat(data.updatedAt()).isNotNull()
                    );
                });
        }

        @Test
        @DisplayName("원두 이름이 없으면 등록이 거부된다")
        void rejectBeanCreationWhenNameIsEmpty() {
            // given
            CreateBeanCommand command = CreateBeanCommand.builder()
                .name("")
                .origin(new Origin("브라질", "세라도"))
                .roaster("커피로스터")
                .roastDate(LocalDate.now())
                .grams(250)
                .flavorIds(List.of(1L, 2L))
                .build();

            // when
            webTestClient.post()
                .uri("/api/beans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<BaseResponse<BeanResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isFalse();
                    assertThat(response.message()).contains("원두 이름은 필수입니다");
                    assertThat(response.data()).isNull();
                });
        }

        @Test
        @DisplayName("원두 이름이 누락되면 등록이 거부된다")
        void rejectBeanCreationWhenNameIsNull() {
            // given
            CreateBeanCommand command = CreateBeanCommand.builder()
                .name(null)
                .origin(new Origin("브라질", "세라도"))
                .roaster("커피로스터")
                .roastDate(LocalDate.now())
                .grams(250)
                .flavorIds(List.of(1L, 2L))
                .build();

            // when
            webTestClient.post()
                .uri("/api/beans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isFalse();
                    assertThat(response.message()).contains("원두 이름은 필수입니다");
                    assertThat(response.data()).isNull();
                });
        }

        @Test
        @DisplayName("원산지 정보가 누락되면 등록이 거부된다")
        void rejectBeanCreationWhenOriginIsNull() {
            // given
            CreateBeanCommand command = CreateBeanCommand.builder()
                .name("테스트 원두")
                .origin(null)
                .roaster("커피로스터")
                .roastDate(LocalDate.now())
                .grams(250)
                .flavorIds(List.of(1L, 2L))
                .build();

            // when
            webTestClient.post()
                .uri("/api/beans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isFalse();
                    assertThat(response.message()).contains("원두 원산지는 필수입니다");
                    assertThat(response.data()).isNull();
                });
        }

        @Test
        @DisplayName("로스터 정보가 없으면 등록이 거부된다")
        void rejectBeanCreationWhenRoasterIsEmpty() {
            // given
            CreateBeanCommand command = CreateBeanCommand.builder()
                .name("테스트 원두")
                .origin(new Origin("과테말라", "안티구아"))
                .roaster("")
                .roastDate(LocalDate.now())
                .grams(250)
                .flavorIds(List.of(1L, 2L))
                .build();

            // when
            webTestClient.post()
                .uri("/api/beans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isFalse();
                    assertThat(response.message()).contains("원두 로스터는 필수입니다");
                    assertThat(response.data()).isNull();
                });
        }

        @Test
        @DisplayName("로스팅 일자가 누락되면 등록이 거부된다")
        void rejectBeanCreationWhenRoastDateIsNull() {
            // given
            CreateBeanCommand command = CreateBeanCommand.builder()
                .name("테스트 원두")
                .origin(new Origin("케냐", "키암부"))
                .roaster("커피로스터")
                .roastDate(null)
                .grams(250)
                .flavorIds(List.of(1L, 2L))
                .build();

            // when
            webTestClient.post()
                .uri("/api/beans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isFalse();
                    assertThat(response.message()).contains("원두 로스팅 일자는 필수입니다");
                    assertThat(response.data()).isNull();
                });
        }

        @Test
        @DisplayName("보유 그램이 음수이면 등록이 거부된다")
        void rejectBeanCreationWhenGramsIsNegative() {
            // given
            CreateBeanCommand command = CreateBeanCommand.builder()
                .name("테스트 원두")
                .origin(new Origin("파나마", "보케테"))
                .roaster("커피로스터")
                .roastDate(LocalDate.now())
                .grams(-100)
                .flavorIds(List.of(1L, 2L))
                .build();

            // when
            webTestClient.post()
                .uri("/api/beans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isFalse();
                    assertThat(response.message()).contains("원두 그램은 0g 이상이어야 합니다");
                    assertThat(response.data()).isNull();
                });
        }

        @Test
        @DisplayName("디카페인 원두도 정상적으로 등록된다")
        void createDecafBeanSuccessfully() {
            // given
            CreateBeanCommand command = CreateBeanCommand.builder()
                .name("디카페인 콜롬비아")
                .origin(new Origin("콜롬비아", "우일라"))
                .roaster("디카페인 전문 로스터")
                .roastDate(LocalDate.now().minusDays(2))
                .grams(200)
                .flavorIds(List.of(1L, 2L))
                .isDecaf(true)
                .processType(ProcessType.WASHED)
                .build();

            // when
            webTestClient.post()
                .uri("/api/beans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<BaseResponse<BeanResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.message()).isBlank();
                    assertThat(response.data()).isNotNull();

                    BeanResponse data = response.data();
                    assertAll(
                        () -> assertThat(data.name()).isEqualTo(command.name()),
                        () -> assertThat(data.origin().country()).isEqualTo(
                            command.origin().country()),
                        () -> assertThat(data.origin().region()).isEqualTo(
                            command.origin().region()),
                        () -> assertThat(data.roaster()).isEqualTo(command.roaster()),
                        () -> assertThat(data.roastDate()).isEqualTo(command.roastDate()),
                        () -> assertThat(data.grams()).isEqualTo(command.grams()),
                        () -> assertThat(data.flavors()).isNotEmpty(),
                        () -> assertThat(data.roastLevel()).isEqualTo(RoastLevel.UNKNOWN),
                        () -> assertThat(data.processType()).isEqualTo(command.processType()),
                        () -> assertThat(data.blendType()).isEqualTo(BlendType.UNKNOWN),
                        () -> assertThat(data.isDecaf()).isEqualTo(command.isDecaf()),
                        () -> assertThat(data.status()).isEqualTo(ActiveStatus.ACTIVE),
                        () -> assertThat(data.memo()).isEqualTo(command.memo()),
                        () -> assertThat(data.createdAt()).isNotNull(),
                        () -> assertThat(data.updatedAt()).isNotNull()
                    );
                });
        }
    }

    @Nested
    @DisplayName("원두 목록 조회")
    class GetAllBeansTest {

        @BeforeEach
        void setUp() {
            createBeans(5);
        }

        @Test
        @DisplayName("기본 페이징으로 원두 목록을 조회한다")
        void getAllBeansWithDefaultPaging() {
            // given

            // when
            webTestClient.get()
                .uri("/api/beans")
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody(
                    new ParameterizedTypeReference<BaseResponse<PageResponse<BeanResponse>>>() {
                    })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.message()).isBlank();
                    assertThat(response.data()).isNotNull();

                    PageResponse<BeanResponse> pageData = response.data();
                    assertAll(
                        () -> assertThat(pageData.page()).isEqualTo(0),
                        () -> assertThat(pageData.content()).hasSize(5)
                    );
                });
        }

        @Test
        @DisplayName("페이지 크기를 지정하여 원두 목록을 조회한다")
        void getAllBeansWithCustomPageSize() {
            // given

            // when
            webTestClient.get()
                .uri("/api/beans?page=0&size=2")
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody(
                    new ParameterizedTypeReference<BaseResponse<PageResponse<BeanResponse>>>() {
                    })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();

                    PageResponse<BeanResponse> pageData = response.data();
                    assertAll(
                        () -> assertThat(pageData.page()).isEqualTo(0),
                        () -> assertThat(pageData.content()).hasSize(2)
                    );
                });
        }

        @Test
        @DisplayName("정렬 조건을 지정하여 원두 목록을 조회한다")
        void getAllBeansWithSorting() {
            // given

            // when
            webTestClient.get()
                .uri("/api/beans?sort=createdAt:desc")
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody(
                    new ParameterizedTypeReference<BaseResponse<PageResponse<BeanResponse>>>() {
                    })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();
                    assertThat(response.data().content()).isNotEmpty();
                });
        }

        @Test
        @DisplayName("원두가 없을 때 빈 목록을 반환한다")
        void returnEmptyListWhenNoBeansExist() {
            cleanUpBeans();

            // when
            webTestClient.get()
                .uri("/api/beans")
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody(
                    new ParameterizedTypeReference<BaseResponse<PageResponse<BeanResponse>>>() {
                    })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();
                    assertThat(response.data().content()).isEmpty();
                });
        }
    }

    @Nested
    @DisplayName("원두 상세 조회")
    class GetBeanTest {

        @Test
        @DisplayName("존재하는 원두 ID로 상세 정보를 조회한다")
        void getBeanById() {
            // given
            Bean bean = createBean();

            // when
            webTestClient.get()
                .uri("/api/beans/{beanId}", bean.id())
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<BaseResponse<BeanResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.message()).isBlank();
                    assertThat(response.data()).isNotNull();

                    BeanResponse data = response.data();
                    assertAll(
                        () -> assertThat(data.beanId()).isEqualTo(bean.id()),
                        () -> assertThat(data.name()).isEqualTo(bean.name()),
                        () -> assertThat(data.createdAt()).isNotNull(),
                        () -> assertThat(data.updatedAt()).isNotNull()
                    );
                });
        }

        @Test
        @DisplayName("존재하지 않는 원두 ID로 조회 시 오류를 반환한다")
        void returnErrorWhenBeanNotFound() {
            // given
            Long nonExistentId = 999L;

            // when
            webTestClient.get()
                .uri("/api/beans/{beanId}", nonExistentId)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isFalse();
                    assertThat(response.message()).contains("원두를 찾을 수 없습니다");
                    assertThat(response.data()).isNull();
                });
        }
    }

    @Nested
    @DisplayName("원두 정보 수정")
    class UpdateBeanTest {

        private Bean bean;

        @BeforeEach
        void setUp() {
            bean = createBean();
        }

        @Test
        @DisplayName("원두 이름을 수정한다")
        void updateBeanName() {
            // given
            UpdateBeanCommand command = UpdateBeanCommand.builder()
                .name("수정된 원두")
                .build();

            // when
            webTestClient.put()
                .uri("/api/beans/{beanId}", bean.id())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<BaseResponse<BeanResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();

                    BeanResponse data = response.data();
                    assertAll(
                        () -> assertThat(data.beanId()).isEqualTo(bean.id()),
                        () -> assertThat(data.name()).isEqualTo("수정된 원두"),
                        () -> assertThat(data.updatedAt()).isNotNull()
                    );
                });
        }

        @Test
        @DisplayName("원두 로스팅 레벨을 수정한다")
        void updateBeanRoastLevel() {
            // given
            UpdateBeanCommand command = UpdateBeanCommand.builder()
                .roastLevel(RoastLevel.DARK)
                .build();

            // when
            webTestClient.put()
                .uri("/api/beans/{beanId}", bean.id())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<BaseResponse<BeanResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();
                    assertThat(response.data().roastLevel()).isEqualTo(RoastLevel.DARK);
                });
        }

        @Test
        @DisplayName("여러 필드를 한번에 수정한다")
        void updateMultipleFields() {
            // given
            UpdateBeanCommand command = UpdateBeanCommand.builder()
                .name("종합 수정 테스트 원두")
                .grams(180)
                .roastLevel(RoastLevel.DARK)
                .memo("여러 필드 수정 테스트")
                .status(ActiveStatus.INACTIVE)
                .build();

            // when
            webTestClient.put()
                .uri("/api/beans/{beanId}", bean.id())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<BaseResponse<BeanResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();

                    BeanResponse data = response.data();
                    assertAll(
                        () -> assertThat(data.name()).isEqualTo("종합 수정 테스트 원두"),
                        () -> assertThat(data.grams()).isEqualTo(180),
                        () -> assertThat(data.roastLevel()).isEqualTo(RoastLevel.DARK),
                        () -> assertThat(data.memo()).isEqualTo("여러 필드 수정 테스트"),
                        () -> assertThat(data.status()).isEqualTo(ActiveStatus.INACTIVE)
                    );
                });
        }

        @Test
        @DisplayName("존재하지 않는 원두 ID로 수정 시 오류를 반환한다")
        void returnErrorWhenUpdatingNonExistentBean() {
            // given
            Long nonExistentId = 999L;
            UpdateBeanCommand command = UpdateBeanCommand.builder()
                .name("수정된 원두")
                .build();

            // when
            webTestClient.put()
                .uri("/api/beans/{beanId}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isFalse();
                    assertThat(response.message()).contains("원두를 찾을 수 없습니다");
                    assertThat(response.data()).isNull();
                });
        }
    }

    @Nested
    @DisplayName("원두 삭제")
    class DeleteBeanTest {

        private Bean bean;

        @BeforeEach
        void setUp() {
            bean = createBean();
        }

        @Test
        @DisplayName("존재하는 원두를 성공적으로 삭제한다")
        void deleteBeanSuccessfully() {
            // given

            // when
            webTestClient.delete()
                .uri("/api/beans/{beanId}", bean.id())
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<BaseResponse<DeleteBeanResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.message()).isBlank();
                    assertThat(response.data()).isNotNull();
                    assertThat(response.data().beanId()).isEqualTo(bean.id());
                });

            // 삭제 후 조회 시 오류 반환 확인
            webTestClient.get()
                .uri("/api/beans/{beanId}", bean.id())
                .exchange()
                .expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("존재하지 않는 원두 ID로 삭제 시 오류를 반환한다")
        void returnErrorWhenDeletingNonExistentBean() {
            // given
            Long nonExistentId = 999L;

            // when
            webTestClient.delete()
                .uri("/api/beans/{beanId}", nonExistentId)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isFalse();
                    assertThat(response.message()).contains("원두를 찾을 수 없습니다");
                    assertThat(response.data()).isNull();
                });
        }
    }
}
