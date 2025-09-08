package io.coffeedia.bootstrap.api.controller.docs;

import io.coffeedia.application.usecase.dto.DeleteEquipmentResponse;
import io.coffeedia.application.usecase.dto.EquipmentResponse;
import io.coffeedia.application.usecase.dto.UpdateEquipmentCommand;
import io.coffeedia.bootstrap.api.controller.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "장비 관리", description = "장비 등록, 수정, 조회, 목록 조회 API")
public interface EquipmentControllerDocs {

    @Operation(
        summary = "장비 상세 조회",
        description = "장비 ID를 이용하여 장비의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "장비 조회 성공",
            content = @Content(schema = @Schema(implementation = EquipmentResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "장비를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = BaseResponse.class))
        )
    })
    ResponseEntity<BaseResponse<EquipmentResponse>> getEquipment(
        @Parameter(description = "장비 ID", required = true, example = "1")
        Long equipmentId
    );

    @Operation(
        summary = "장비 정보 수정",
        description = "기존 장비의 정보를 수정합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "장비 수정 성공",
            content = @Content(schema = @Schema(implementation = EquipmentResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "장비를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = BaseResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = @Content(schema = @Schema(implementation = BaseResponse.class))
        )
    })
    ResponseEntity<BaseResponse<EquipmentResponse>> updateEquipment(
        @Parameter(description = "장비 ID", required = true, example = "1")
        Long equipmentId,

        @Parameter(description = "수정할 장비 정보", required = true)
        UpdateEquipmentCommand command
    );

    @Operation(
        summary = "장비 삭제",
        description = "장비를 삭제합니다. (소프트 삭제)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "장비 삭제 성공",
            content = @Content(schema = @Schema(implementation = DeleteEquipmentResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "장비를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = BaseResponse.class))
        )
    })
    ResponseEntity<BaseResponse<DeleteEquipmentResponse>> deleteEquipment(
        @Parameter(description = "장비 ID", required = true, example = "1")
        Long equipmentId
    );
}
