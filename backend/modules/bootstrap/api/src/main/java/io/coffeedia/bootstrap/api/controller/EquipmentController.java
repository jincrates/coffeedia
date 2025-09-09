package io.coffeedia.bootstrap.api.controller;

import static io.coffeedia.common.constant.CommonConstant.USER_ID;

import io.coffeedia.application.usecase.CreateEquipmentUseCase;
import io.coffeedia.application.usecase.DeleteEquipmentUseCase;
import io.coffeedia.application.usecase.GetAllEquipmentsUseCase;
import io.coffeedia.application.usecase.GetEquipmentUseCase;
import io.coffeedia.application.usecase.UpdateEquipmentUseCase;
import io.coffeedia.application.usecase.dto.CreateEquipmentCommand;
import io.coffeedia.application.usecase.dto.DeleteEquipmentCommand;
import io.coffeedia.application.usecase.dto.DeleteEquipmentResponse;
import io.coffeedia.application.usecase.dto.EquipmentResponse;
import io.coffeedia.application.usecase.dto.EquipmentSearchQuery;
import io.coffeedia.application.usecase.dto.UpdateEquipmentCommand;
import io.coffeedia.bootstrap.api.controller.docs.EquipmentControllerDocs;
import io.coffeedia.bootstrap.api.controller.dto.BaseResponse;
import io.coffeedia.bootstrap.api.controller.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/equipments")
public class EquipmentController extends BaseController implements EquipmentControllerDocs {

    private final CreateEquipmentUseCase createUseCase;
    private final GetAllEquipmentsUseCase getAllUseCase;
    private final GetEquipmentUseCase getUseCase;
    private final UpdateEquipmentUseCase updateUseCase;
    private final DeleteEquipmentUseCase deleteUseCase;

    @PostMapping
    public ResponseEntity<BaseResponse<EquipmentResponse>> createEquipment(
        @Valid @RequestBody CreateEquipmentCommand command
    ) {
        return created(createUseCase.invoke(command.withUserId(USER_ID)));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<EquipmentResponse>>> getAllEquipments(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        var response = getAllUseCase.invoke(EquipmentSearchQuery.of(page, size));
        return ok(PageResponse.of(page, size, response));
    }

    @Override
    @GetMapping("/{equipmentId}")
    public ResponseEntity<BaseResponse<EquipmentResponse>> getEquipment(
        @PathVariable Long equipmentId
    ) {
        return ok(getUseCase.invoke(equipmentId));
    }

    @Override
    @PutMapping("/{equipmentId}")
    public ResponseEntity<BaseResponse<EquipmentResponse>> updateEquipment(
        @PathVariable Long equipmentId,
        @Valid @RequestBody UpdateEquipmentCommand command
    ) {
        return ok(updateUseCase.invoke(command.withEquipmentId(equipmentId).withUserId(USER_ID)));
    }

    @Override
    @DeleteMapping("/{equipmentId}")
    public ResponseEntity<BaseResponse<DeleteEquipmentResponse>> deleteEquipment(
        @PathVariable Long equipmentId
    ) {
        return ok(deleteUseCase.invoke(DeleteEquipmentCommand.of(equipmentId, USER_ID)));
    }
}
