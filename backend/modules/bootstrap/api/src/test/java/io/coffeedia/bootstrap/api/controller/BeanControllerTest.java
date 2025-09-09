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
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;

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

            // when & then
            authenticatedPost("/api/beans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
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
        @DisplayName("인증 없이 원두 등록 시 401 Unauthorized를 반환한다")
        void createBeanWithoutAuthenticationReturns401() {
            // given
            CreateBeanCommand command = CreateBeanCommand.builder()
                .name("에티오피아 예가체프")
                .origin(new Origin("에티오피아", "예가체프"))
                .roaster("커피로스터")
                .roastDate(LocalDate.now().minusDays(3))
                .grams(250)
                .flavorIds(List.of(1L, 2L))
                .build();

            // when & then
            webTestClient.post()
                .uri("/api/beans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isUnauthorized();
        }

        @Test
        @DisplayName("인증 없이 원두 상세 조회 시 401 Unauthorized를 반환한다")
        void getBeanWithoutAuthenticationReturns401() {
            // given
            Bean bean = createBean();

            // when & then
            webTestClient.get()
                .uri("/api/beans/{beanId}", bean.id())
                .exchange()
                .expectStatus().isUnauthorized();
        }

        @Test
        @DisplayName("인증 없이 원두 수정 시 401 Unauthorized를 반환한다")
        void updateBeanWithoutAuthenticationReturns401() {
            // given
            Bean bean = createBean();
            UpdateBeanCommand command = UpdateBeanCommand.builder()
                .name("수정된 원두")
                .build();

            // when & then
            webTestClient.put()
                .uri("/api/beans/{beanId}", bean.id())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isUnauthorized();
        }

        @Test
        @DisplayName("인증 없이 원두 삭제 시 401 Unauthorized를 반환한다")
        void deleteBeanWithoutAuthenticationReturns401() {
            // given
            Bean bean = createBean();

            // when & then
            webTestClient.delete()
                .uri("/api/beans/{beanId}", bean.id())
                .exchange()
                .expectStatus().isUnauthorized();
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
            // when & then
            authenticatedGet("/api/beans")
                .exchange()
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
        @DisplayName("인증 없이 원두 목록 조회 시 401 Unauthorized를 반환한다")
        void getAllBeansWithoutAuthenticationReturns401() {
            // when & then
            webTestClient.get()
                .uri("/api/beans")
                .exchange()
                .expectStatus().isUnauthorized();
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

            // when & then
            authenticatedGet("/api/beans/{beanId}", bean.id())
                .exchange()
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

            // when & then
            authenticatedPut("/api/beans/{beanId}", bean.id())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
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
            // when & then
            authenticatedDelete("/api/beans/{beanId}", bean.id())
                .exchange()
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
            authenticatedGet("/api/beans/{beanId}", bean.id())
                .exchange()
                .expectStatus().isBadRequest();
        }
    }
}
