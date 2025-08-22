package io.coffeedia.bootstrap.api;

import io.coffeedia.application.usecase.CreateBeanUseCase;
import io.coffeedia.application.usecase.dto.CreateBeanCommand;
import io.coffeedia.application.usecase.dto.CreateBeanResponse;
import io.coffeedia.bootstrap.api.dto.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/beans")
public class BeanController extends BaseController {

    private final CreateBeanUseCase createUseCase;

    @PostMapping
    public ResponseEntity<BaseResponse<CreateBeanResponse>> createBean(
        @Valid @RequestBody CreateBeanCommand command
    ) {
        return created(createUseCase.invoke(command));
    }
}
