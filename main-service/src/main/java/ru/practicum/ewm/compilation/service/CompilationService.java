package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.compilation.PublicCompilationParam;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {

    Compilation create(NewCompilationDto compilation);

    void delete(Long id);

    Compilation update(UpdateCompilationRequest updateCompilationRequest);

    List<Compilation> getAll(PublicCompilationParam publicCompilationParam);

    Compilation getById(Long id);

}
