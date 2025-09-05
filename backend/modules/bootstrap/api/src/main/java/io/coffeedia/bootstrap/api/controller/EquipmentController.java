package io.coffeedia.bootstrap.api.controller;

import static io.coffeedia.common.constant.CommonConstant.USER_ID;

import io.coffeedia.application.usecase.CreateEquipmentUseCase;
import io.coffeedia.application.usecase.dto.CreateEquipmentCommand;
import io.coffeedia.application.usecase.dto.EquipmentResponse;
import io.coffeedia.bootstrap.api.controller.dto.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/equipments")
public class EquipmentController extends BaseController {

    private final CreateEquipmentUseCase createUseCase;

    @PostMapping
    public ResponseEntity<BaseResponse<EquipmentResponse>> createEquipment(
        @Valid @RequestBody CreateEquipmentCommand command
    ) {
        return created(createUseCase.invoke(command.withUserId(USER_ID)));
    }
}
