package io.coffeedia.bootstrap.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.coffeedia.IntegrationSupportTest;
import io.coffeedia.application.usecase.dto.CreateEquipmentCommand;
import io.coffeedia.application.usecase.dto.DeleteEquipmentResponse;
import io.coffeedia.application.usecase.dto.EquipmentResponse;
import io.coffeedia.application.usecase.dto.UpdateEquipmentCommand;
import io.coffeedia.bootstrap.api.controller.dto.BaseResponse;
import io.coffeedia.bootstrap.api.controller.dto.PageResponse;
import io.coffeedia.domain.model.Equipment;
import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.EquipmentType;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;

class EquipmentControllerTest extends IntegrationSupportTest {

    @BeforeEach
    void setUp() {
        cleanUpEquipments();
    }

    @Nested
    @DisplayName("장비 등록")
    class CreateEquipmentTest {

        @Test
        @DisplayName("필수 정보만으로도 장비가 정상적으로 등록된다")
        void createEquipmentWithRequiredFieldsOnly() {
            // given
            CreateEquipmentCommand command = CreateEquipmentCommand.builder()
                .type(EquipmentType.GRINDER)
                .name("바라짜 엔코어")
                .brand("바라짜")
                .build();

            // when
            webTestClient.post()
                .uri("/api/equipments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<BaseResponse<EquipmentResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.message()).isBlank();
                    assertThat(response.data()).isNotNull();

                    EquipmentResponse data = response.data();
                    assertAll(
                        () -> assertThat(data.type()).isEqualTo(command.type()),
                        () -> assertThat(data.name()).isEqualTo(command.name()),
                        () -> assertThat(data.brand()).isEqualTo(command.brand()),
                        () -> assertThat(data.status()).isEqualTo(ActiveStatus.ACTIVE),
                        () -> assertThat(data.description()).isNull(),
                        () -> assertThat(data.buyDate()).isNull(),
                        () -> assertThat(data.buyUrl()).isNull(),
                        () -> assertThat(data.createdAt()).isNotNull(),
                        () -> assertThat(data.updatedAt()).isNotNull()
                    );
                });
        }

        @Test
        @DisplayName("모든 장비 정보를 입력하면 완전한 장비 프로필이 생성된다")
        void createEquipmentWithAllFields() {
            // given
            CreateEquipmentCommand command = CreateEquipmentCommand.builder()
                .type(EquipmentType.MACHINE)
                .name("브레빌 바리스타 익스프레스")
                .brand("브레빌")
                .status(ActiveStatus.ACTIVE)
                .description("반자동 에스프레소 머신")
                .buyDate(LocalDate.now().minusMonths(6))
                .buyUrl("https://example.com/product")
                .build();

            // when
            webTestClient.post()
                .uri("/api/equipments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<BaseResponse<EquipmentResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.message()).isBlank();
                    assertThat(response.data()).isNotNull();

                    EquipmentResponse data = response.data();
                    assertAll(
                        () -> assertThat(data.type()).isEqualTo(command.type()),
                        () -> assertThat(data.name()).isEqualTo(command.name()),
                        () -> assertThat(data.brand()).isEqualTo(command.brand()),
                        () -> assertThat(data.status()).isEqualTo(command.status()),
                        () -> assertThat(data.description()).isEqualTo(command.description()),
                        () -> assertThat(data.buyDate()).isEqualTo(command.buyDate()),
                        () -> assertThat(data.buyUrl()).isEqualTo(command.buyUrl()),
                        () -> assertThat(data.createdAt()).isNotNull(),
                        () -> assertThat(data.updatedAt()).isNotNull()
                    );
                });
        }

        @Test
        @DisplayName("장비 타입이 누락되면 등록이 거부된다")
        void rejectEquipmentCreationWhenTypeIsNull() {
            // given
            CreateEquipmentCommand command = CreateEquipmentCommand.builder()
                .type(null)
                .name("테스트 장비")
                .brand("테스트 브랜드")
                .build();

            // when
            webTestClient.post()
                .uri("/api/equipments")
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
                    assertThat(response.message()).contains("장비 타입은 필수입니다");
                    assertThat(response.data()).isNull();
                });
        }

        @Test
        @DisplayName("장비 이름이 없으면 등록이 거부된다")
        void rejectEquipmentCreationWhenNameIsEmpty() {
            // given
            CreateEquipmentCommand command = CreateEquipmentCommand.builder()
                .type(EquipmentType.GRINDER)
                .name("")
                .brand("테스트 브랜드")
                .build();

            // when
            webTestClient.post()
                .uri("/api/equipments")
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
                    assertThat(response.message()).contains("장비 이름은 필수입니다");
                    assertThat(response.data()).isNull();
                });
        }

        @Test
        @DisplayName("브랜드 정보가 없으면 등록이 거부된다")
        void rejectEquipmentCreationWhenBrandIsEmpty() {
            // given
            CreateEquipmentCommand command = CreateEquipmentCommand.builder()
                .type(EquipmentType.SCALE)
                .name("테스트 저울")
                .brand("")
                .build();

            // when
            webTestClient.post()
                .uri("/api/equipments")
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
                    assertThat(response.message()).contains("장비 브랜드는 필수입니다");
                    assertThat(response.data()).isNull();
                });
        }

        @Test
        @DisplayName("미래 구매일자로 등록이 거부된다")
        void rejectEquipmentCreationWhenBuyDateIsFuture() {
            // given
            CreateEquipmentCommand command = CreateEquipmentCommand.builder()
                .type(EquipmentType.MACHINE)
                .name("전기 케틀")
                .brand("펠로우")
                .buyDate(LocalDate.now().plusDays(1))
                .build();

            // when
            webTestClient.post()
                .uri("/api/equipments")
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
                    assertThat(response.message()).contains("구매일자는 현재 날짜보다 이후일 수 없습니다.");
                    assertThat(response.data()).isNull();
                });
        }

        @Test
        @DisplayName("잘못된 URL 형식으로 등록이 거부된다")
        void rejectEquipmentCreationWhenBuyUrlIsInvalid() {
            // given
            CreateEquipmentCommand command = CreateEquipmentCommand.builder()
                .type(EquipmentType.GRINDER)
                .name("전동 그라인더")
                .brand("커맨단테")
                .buyUrl("invalid-url")
                .build();

            // when
            webTestClient.post()
                .uri("/api/equipments")
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
                    assertThat(response.message()).contains("유효한 URL 형식이어야 합니다.");
                    assertThat(response.data()).isNull();
                });
        }
    }

    @Nested
    @DisplayName("장비 목록 조회")
    class GetAllEquipmentsTest {

        @BeforeEach
        void setUp() {
            createEquipments(5);
        }

        @Test
        @DisplayName("기본 페이징으로 장비 목록을 조회한다")
        void getAllEquipmentsWithDefaultPaging() {
            // given

            // when
            webTestClient.get()
                .uri("/api/equipments")
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody(
                    new ParameterizedTypeReference<BaseResponse<PageResponse<EquipmentResponse>>>() {
                    })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.message()).isBlank();
                    assertThat(response.data()).isNotNull();

                    PageResponse<EquipmentResponse> pageData = response.data();
                    assertAll(
                        () -> assertThat(pageData.page()).isEqualTo(0),
                        () -> assertThat(pageData.content()).hasSize(5)
                    );
                });
        }

        @Test
        @DisplayName("페이지 크기를 지정하여 장비 목록을 조회한다")
        void getAllEquipmentsWithCustomPageSize() {
            // given

            // when
            webTestClient.get()
                .uri("/api/equipments?page=0&size=2")
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody(
                    new ParameterizedTypeReference<BaseResponse<PageResponse<EquipmentResponse>>>() {
                    })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();

                    PageResponse<EquipmentResponse> pageData = response.data();
                    assertAll(
                        () -> assertThat(pageData.page()).isEqualTo(0),
                        () -> assertThat(pageData.content()).hasSize(2)
                    );
                });
        }

        @Test
        @DisplayName("장비가 없을 때 빈 목록을 반환한다")
        void returnEmptyListWhenNoEquipmentsExist() {
            cleanUpEquipments();

            // when
            webTestClient.get()
                .uri("/api/equipments")
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody(
                    new ParameterizedTypeReference<BaseResponse<PageResponse<EquipmentResponse>>>() {
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
    @DisplayName("장비 상세 조회")
    class GetEquipmentTest {

        @Test
        @DisplayName("존재하는 장비 ID로 상세 정보를 조회한다")
        void getEquipmentById() {
            // given
            Equipment equipment = createEquipment();

            // when
            webTestClient.get()
                .uri("/api/equipments/{equipmentId}", equipment.id())
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<BaseResponse<EquipmentResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.message()).isBlank();
                    assertThat(response.data()).isNotNull();

                    EquipmentResponse data = response.data();
                    assertAll(
                        () -> assertThat(data.id()).isEqualTo(equipment.id()),
                        () -> assertThat(data.type()).isEqualTo(equipment.type()),
                        () -> assertThat(data.name()).isEqualTo(equipment.name()),
                        () -> assertThat(data.brand()).isEqualTo(equipment.brand()),
                        () -> assertThat(data.createdAt()).isNotNull(),
                        () -> assertThat(data.updatedAt()).isNotNull()
                    );
                });
        }

        @Test
        @DisplayName("존재하지 않는 장비 ID로 조회 시 오류를 반환한다")
        void returnErrorWhenEquipmentNotFound() {
            // given
            Long nonExistentId = 999L;

            // when
            webTestClient.get()
                .uri("/api/equipments/{equipmentId}", nonExistentId)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isFalse();
                    assertThat(response.message()).contains("장비를 찾을 수 없습니다");
                    assertThat(response.data()).isNull();
                });
        }
    }

    @Nested
    @DisplayName("장비 정보 수정")
    class UpdateEquipmentTest {

        private Equipment equipment;

        @BeforeEach
        void setUp() {
            equipment = createEquipment();
        }

        @Test
        @DisplayName("장비 이름을 수정한다")
        void updateEquipmentName() {
            // given
            UpdateEquipmentCommand command = UpdateEquipmentCommand.builder()
                .type(equipment.type())
                .name("수정된 장비명")
                .brand(equipment.brand())
                .build();

            // when
            webTestClient.put()
                .uri("/api/equipments/{equipmentId}", equipment.id())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<BaseResponse<EquipmentResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();

                    EquipmentResponse data = response.data();
                    assertAll(
                        () -> assertThat(data.id()).isEqualTo(equipment.id()),
                        () -> assertThat(data.name()).isEqualTo("수정된 장비명"),
                        () -> assertThat(data.updatedAt()).isNotNull()
                    );
                });
        }

        @Test
        @DisplayName("장비 설명과 구매 정보를 수정한다")
        void updateEquipmentDescriptionAndBuyInfo() {
            // given
            UpdateEquipmentCommand command = UpdateEquipmentCommand.builder()
                .type(equipment.type())
                .name(equipment.name())
                .brand(equipment.brand())
                .description("업데이트된 상세 설명")
                .buyDate(LocalDate.now().minusYears(1))
                .buyUrl("https://updated-url.com")
                .build();

            // when
            webTestClient.put()
                .uri("/api/equipments/{equipmentId}", equipment.id())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<BaseResponse<EquipmentResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();

                    EquipmentResponse data = response.data();
                    assertAll(
                        () -> assertThat(data.description()).isEqualTo("업데이트된 상세 설명"),
                        () -> assertThat(data.buyDate()).isEqualTo(LocalDate.now().minusYears(1)),
                        () -> assertThat(data.buyUrl()).isEqualTo("https://updated-url.com")
                    );
                });
        }

        @Test
        @DisplayName("여러 필드를 한번에 수정한다")
        void updateMultipleFields() {
            // given
            UpdateEquipmentCommand command = UpdateEquipmentCommand.builder()
                .type(EquipmentType.SCALE)
                .name("종합 수정 테스트 장비")
                .brand("업데이트된 브랜드")
                .description("여러 필드 수정 테스트")
                .buyDate(LocalDate.now().minusMonths(3))
                .buyUrl("https://comprehensive-update.com")
                .build();

            // when
            webTestClient.put()
                .uri("/api/equipments/{equipmentId}", equipment.id())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<BaseResponse<EquipmentResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();

                    EquipmentResponse data = response.data();
                    assertAll(
                        () -> assertThat(data.type()).isEqualTo(EquipmentType.SCALE),
                        () -> assertThat(data.name()).isEqualTo("종합 수정 테스트 장비"),
                        () -> assertThat(data.brand()).isEqualTo("업데이트된 브랜드"),
                        () -> assertThat(data.description()).isEqualTo("여러 필드 수정 테스트"),
                        () -> assertThat(data.buyDate()).isEqualTo(LocalDate.now().minusMonths(3)),
                        () -> assertThat(data.buyUrl()).isEqualTo(
                            "https://comprehensive-update.com")
                    );
                });
        }

        @Test
        @DisplayName("존재하지 않는 장비 ID로 수정 시 오류를 반환한다")
        void returnErrorWhenUpdatingNonExistentEquipment() {
            // given
            Long nonExistentId = 999L;
            UpdateEquipmentCommand command = UpdateEquipmentCommand.builder()
                .type(EquipmentType.GRINDER)
                .name("수정된 장비")
                .brand("테스트 브랜드")
                .build();

            // when
            webTestClient.put()
                .uri("/api/equipments/{equipmentId}", nonExistentId)
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
                    assertThat(response.message()).contains("장비를 찾을 수 없습니다");
                    assertThat(response.data()).isNull();
                });
        }
    }

    @Nested
    @DisplayName("장비 삭제")
    class DeleteEquipmentTest {

        private Equipment equipment;

        @BeforeEach
        void setUp() {
            equipment = createEquipment();
        }

        @Test
        @DisplayName("존재하는 장비를 성공적으로 삭제한다")
        void deleteEquipmentSuccessfully() {
            // given

            // when
            webTestClient.delete()
                .uri("/api/equipments/{equipmentId}", equipment.id())
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody(
                    new ParameterizedTypeReference<BaseResponse<DeleteEquipmentResponse>>() {
                    })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.message()).isBlank();
                    assertThat(response.data()).isNotNull();
                    assertThat(response.data().equipmentId()).isEqualTo(equipment.id());
                    assertThat(response.data().message()).contains("성공적으로 삭제되었습니다");
                });
        }

        @Test
        @DisplayName("존재하지 않는 장비 ID로 삭제 시 오류를 반환한다")
        void returnErrorWhenDeletingNonExistentEquipment() {
            // given
            Long nonExistentId = 999L;

            // when
            webTestClient.delete()
                .uri("/api/equipments/{equipmentId}", nonExistentId)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isFalse();
                    assertThat(response.message()).contains("장비를 찾을 수 없습니다");
                    assertThat(response.data()).isNull();
                });
        }
    }
}
