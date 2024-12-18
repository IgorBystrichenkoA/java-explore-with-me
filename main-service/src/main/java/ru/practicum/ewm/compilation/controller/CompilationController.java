package ru.practicum.ewm.compilation.controller;

import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.compilation.CompilationMapper;
import ru.practicum.ewm.compilation.PublicCompilationParam;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class CompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(PublicCompilationParam publicCompilationParam) {
        List<Compilation> compilationList = compilationService.getAll(publicCompilationParam);
        return compilationList.stream().map(CompilationMapper::toCompilationDto).toList();
    }

    @GetMapping("/{id}")
    public CompilationDto getCompilationById(@PathVariable Long id) {
        return CompilationMapper.toCompilationDto(compilationService.getById(id));
    }
}
