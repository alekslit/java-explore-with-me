package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewOrUpdateCompilationDto;
import ru.practicum.ewm.compilation.service.AdminCompilationService;
import ru.practicum.ewm.compilation.service.PublicCompilationService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CompilationController {
    private final AdminCompilationService adminService;
    private final PublicCompilationService publicService;

    /*--------------------Основные Admin методы--------------------*/
    @PostMapping("/admin/compilations")
    public CompilationDto saveCompilation(@Valid @RequestBody NewOrUpdateCompilationDto compilationDto) {
        return adminService.saveCompilation(compilationDto);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationDto updateCompilation(@Valid @RequestBody NewOrUpdateCompilationDto compilationDto,
                                            @PathVariable Long compId) {
        return adminService.updateCompilation(compilationDto, compId);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    public String deleteCompilation(@PathVariable Long compId) {
        return adminService.deleteCompilation(compId);
    }

    /*--------------------Основные Public методы--------------------*/
    @GetMapping("/compilations")
    public List<CompilationDto> getAllCompilations(@RequestParam Boolean pinned,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return publicService.getAllCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        return publicService.getCompilationById(compId);
    }
}