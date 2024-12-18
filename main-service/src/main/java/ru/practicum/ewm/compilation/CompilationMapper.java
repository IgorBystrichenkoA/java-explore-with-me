package ru.practicum.ewm.compilation;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.event.EventMapper;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;

@UtilityClass
public class CompilationMapper {

    public CompilationDto toCompilationDto(Compilation compilation) {

        CompilationDto compilationDto = new CompilationDto();
        if (compilation.getEvents() != null) {
            compilationDto.setEvents(EventMapper.toListEventShortDto(compilation.getEvents()));
        } else {
            compilationDto.setEvents(new ArrayList<>());
        }
        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());

        return compilationDto;
    }
}