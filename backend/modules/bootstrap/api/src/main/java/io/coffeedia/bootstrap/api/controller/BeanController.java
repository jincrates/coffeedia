package io.coffeedia.bootstrap.api.controller;

import io.coffeedia.application.usecase.CreateBeanUseCase;
import io.coffeedia.application.usecase.GetAllBeanUseCase;
import io.coffeedia.application.usecase.GetBeanUseCase;
import io.coffeedia.application.usecase.UpdateBeanUseCase;
import io.coffeedia.application.usecase.dto.BeanResponse;
import io.coffeedia.application.usecase.dto.BeanSearchQuery;
import io.coffeedia.application.usecase.dto.CreateBeanCommand;
import io.coffeedia.application.usecase.dto.UpdateBeanCommand;
import io.coffeedia.bootstrap.api.controller.docs.BeanControllerDocs;
import io.coffeedia.bootstrap.api.controller.dto.BaseResponse;
import io.coffeedia.bootstrap.api.controller.dto.PageResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/beans")
public class BeanController extends BaseController implements BeanControllerDocs {

    private final CreateBeanUseCase createUseCase;
    private final GetAllBeanUseCase getAllUseCase;
    private final GetBeanUseCase getUseCase;
    private final UpdateBeanUseCase updateUseCase;

    @Override
    @PostMapping
    public ResponseEntity<BaseResponse<BeanResponse>> createBean(
        @Valid @RequestBody CreateBeanCommand command
    ) {
        return created(createUseCase.invoke(command));
    }

    @Override
    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<BeanResponse>>> getAllBeans(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String sort  // field1:asc,field2:desc
    ) {
        List<BeanResponse> response = getAllUseCase.invoke(BeanSearchQuery.of(page, size, sort));
        return ok(PageResponse.of(page, size, response));
    }

    @Override
    @GetMapping("/{beanId}")
    public ResponseEntity<BaseResponse<BeanResponse>> getBean(
        @PathVariable Long beanId
    ) {
        return ok(getUseCase.invoke(beanId));
    }

    @Override
    @PutMapping("/{beanId}")
    public ResponseEntity<BaseResponse<BeanResponse>> updateBean(
        @PathVariable Long beanId,
        @Valid @RequestBody UpdateBeanCommand command
    ) {
        return ok(updateUseCase.invoke(command.withId(beanId)));
    }
}
