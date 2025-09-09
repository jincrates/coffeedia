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

            // when & then
            authenticatedPost("/api/equipments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
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
        @DisplayName("인증 없이 장비 등록 시 401 Unauthorized를 반환한다")
        void createEquipmentWithoutAuthenticationReturns401() {
            // given
            CreateEquipmentCommand command = CreateEquipmentCommand.builder()
                .type(EquipmentType.GRINDER)
                .name("바라짜 엔코어")
                .brand("바라짜")
                .build();

            // when & then
            webTestClient.post()
                .uri("/api/equipments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isUnauthorized();
        }

        @Test
        @DisplayName("모든 장비 정보를 입력하면 완전한 장비 프로필이 생성된다")
        void createEquipmentWithAllFields() {
            // given
            CreateEquipmentCommand command = CreateEquipmentCommand.builder()
                .type(EquipmentType.MACHINE)
                .name("로켓 아파르타멘토")
                .brand("로켓 에스프레소")
                .description("가정용 듀얼 보일러 에스프레소 머신")
                .buyDate(LocalDate.now().minusMonths(3))
                .buyUrl("https://example.com/rocket-apartamento")
                .status(ActiveStatus.ACTIVE)
                .build();

            // when & then
            authenticatedPost("/api/equipments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
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
                        () -> assertThat(data.description()).isEqualTo(command.description()),
                        () -> assertThat(data.buyDate()).isEqualTo(command.buyDate()),
                        () -> assertThat(data.buyUrl()).isEqualTo(command.buyUrl()),
                        () -> assertThat(data.status()).isEqualTo(command.status()),
                        () -> assertThat(data.createdAt()).isNotNull(),
                        () -> assertThat(data.updatedAt()).isNotNull()
                    );
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
            // when & then
            authenticatedGet("/api/equipments")
                .exchange()
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
        @DisplayName("인증 없이 장비 목록 조회 시 401 Unauthorized를 반환한다")
        void getAllEquipmentsWithoutAuthenticationReturns401() {
            // when & then
            webTestClient.get()
                .uri("/api/equipments")
                .exchange()
                .expectStatus().isUnauthorized();
        }

        @Test
        @DisplayName("페이지 크기를 지정하여 장비 목록을 조회한다")
        void getAllEquipmentsWithCustomPageSize() {
            // when & then
            authenticatedGet("/api/equipments?page=0&size=2")
                .exchange()
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

            // when & then
            authenticatedGet("/api/equipments")
                .exchange()
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

            // when & then
            authenticatedGet("/api/equipments/{equipmentId}", equipment.id())
                .exchange()
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
                        () -> assertThat(data.equipmentId()).isEqualTo(equipment.id()),
                        () -> assertThat(data.name()).isEqualTo(equipment.name()),
                        () -> assertThat(data.type()).isEqualTo(equipment.type()),
                        () -> assertThat(data.brand()).isEqualTo(equipment.brand()),
                        () -> assertThat(data.createdAt()).isNotNull(),
                        () -> assertThat(data.updatedAt()).isNotNull()
                    );
                });
        }

        @Test
        @DisplayName("인증 없이 장비 상세 조회 시 401 Unauthorized를 반환한다")
        void getEquipmentWithoutAuthenticationReturns401() {
            // given
            Equipment equipment = createEquipment();

            // when & then
            webTestClient.get()
                .uri("/api/equipments/{equipmentId}", equipment.id())
                .exchange()
                .expectStatus().isUnauthorized();
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
                .name("수정된 장비")
                .type(EquipmentType.MACHINE)
                .build();

            // when & then
            authenticatedPut("/api/equipments/{equipmentId}", equipment.id())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<BaseResponse<EquipmentResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();

                    EquipmentResponse data = response.data();
                    assertAll(
                        () -> assertThat(data.equipmentId()).isEqualTo(equipment.id()),
                        () -> assertThat(data.name()).isEqualTo("수정된 장비"),
                        () -> assertThat(data.updatedAt()).isNotNull()
                    );
                });
        }

        @Test
        @DisplayName("인증 없이 장비 수정 시 401 Unauthorized를 반환한다")
        void updateEquipmentWithoutAuthenticationReturns401() {
            // given
            UpdateEquipmentCommand command = UpdateEquipmentCommand.builder()
                .name("수정된 장비")
                .build();

            // when & then
            webTestClient.put()
                .uri("/api/equipments/{equipmentId}", equipment.id())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isUnauthorized();
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
            // when & then
            authenticatedDelete("/api/equipments/{equipmentId}", equipment.id())
                .exchange()
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
                });
        }

        @Test
        @DisplayName("인증 없이 장비 삭제 시 401 Unauthorized를 반환한다")
        void deleteEquipmentWithoutAuthenticationReturns401() {
            // when & then
            webTestClient.delete()
                .uri("/api/equipments/{equipmentId}", equipment.id())
                .exchange()
                .expectStatus().isUnauthorized();
        }
    }
}
