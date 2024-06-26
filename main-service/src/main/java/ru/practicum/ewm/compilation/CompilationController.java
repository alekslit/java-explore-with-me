package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.service.AdminCompilationService;
import ru.practicum.ewm.compilation.service.PublicCompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class CompilationController {
    private final AdminCompilationService adminService;
    private final PublicCompilationService publicService;

    /*--------------------Основные Admin методы--------------------*/
    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto saveCompilation(@Valid @RequestBody NewCompilationDto compilationDto) {
        return CompilationMapper.mapToCompilationDto(adminService.saveCompilation(compilationDto));
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationDto updateCompilation(@Valid @RequestBody UpdateCompilationRequest compilationRequest,
                                            @PathVariable Long compId) {
        return CompilationMapper.mapToCompilationDto(adminService.updateCompilation(compilationRequest, compId));
    }

    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String deleteCompilation(@PathVariable Long compId) {
        return adminService.deleteCompilation(compId);
    }

    /*--------------------Основные Public методы--------------------*/
    @GetMapping("/compilations")
    public List<CompilationDto> getAllCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "Параметр запроса from, должен быть " +
                    "положительным числом или нулём.") Integer from,
            @RequestParam(defaultValue = "10") @Positive(message = "Параметр запроса size, должен быть " +
                    "положительным числом.") Integer size) {
        return CompilationMapper.mapToCompilationDto(publicService.getAllCompilations(pinned, from, size));
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        return CompilationMapper.mapToCompilationDto(publicService.getCompilationById(compId));
    }
}