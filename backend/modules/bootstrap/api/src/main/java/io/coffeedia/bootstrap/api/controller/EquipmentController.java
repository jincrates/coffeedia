package io.coffeedia.bootstrap.api.controller;

import static io.coffeedia.common.constant.CommonConstant.USER_ID;

import io.coffeedia.application.usecase.CreateEquipmentUseCase;
import io.coffeedia.application.usecase.GetAllEquipmentsUseCase;
import io.coffeedia.application.usecase.dto.CreateEquipmentCommand;
import io.coffeedia.application.usecase.dto.EquipmentResponse;
import io.coffeedia.application.usecase.dto.EquipmentSearchQuery;
import io.coffeedia.bootstrap.api.controller.dto.BaseResponse;
import io.coffeedia.bootstrap.api.controller.dto.PageResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/equipments")
public class EquipmentController extends BaseController {

    private final CreateEquipmentUseCase createUseCase;
    private final GetAllEquipmentsUseCase getAllUseCase;

    @PostMapping
    public ResponseEntity<BaseResponse<EquipmentResponse>> createEquipment(
        @Valid @RequestBody CreateEquipmentCommand command
    ) {
        return created(createUseCase.invoke(command.withUserId(USER_ID)));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<EquipmentResponse>>> getAllEquipments(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String sort  // field1:asc,field2:desc
    ) {
        List<EquipmentResponse> response = getAllUseCase.invoke(
            EquipmentSearchQuery.of(page, size));
        return ok(PageResponse.of(page, size, response));
    }
}
