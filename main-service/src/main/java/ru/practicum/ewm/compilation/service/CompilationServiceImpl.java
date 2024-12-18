package ru.practicum.ewm.compilation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.compilation.CompilationRepository;
import ru.practicum.ewm.compilation.PublicCompilationParam;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.data.OffsetBasedPageRequest;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

    private static final String NOT_EXISTING_COMPILATION = "Подборка не найдена или недоступна";

    @Override
    @Transactional
    public Compilation create(NewCompilationDto compilationDto) {
        log.info("ADMIN: Запрос на создание подборки события {}", compilationDto);

        List<Event> events = compilationDto.getEvents() != null ?
                eventRepository.getAllByIdIn(compilationDto.getEvents()) : List.of();

        Compilation compilationToSave = Compilation.builder()
                .pinned(compilationDto.getPinned())
                .title(compilationDto.getTitle())
                .eventList(compilationDto.getEvents())
                .events(events)
                .build();
        Compilation compilationSaved = compilationRepository.save(compilationToSave);
        log.info("Подборка событий {} создана", compilationDto);

        compilationSaved.setEvents(compilationToSave.getEvents());
        return compilationSaved;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("ADMIN: Запрос на удаление подборки события");

        compilationRepository.getCompilationById(id).orElseThrow(() -> new NotFoundException(NOT_EXISTING_COMPILATION));

        compilationRepository.deleteById(id);
        log.info("Подборка событий удалена");
    }

    @Override
    @Transactional
    public Compilation update(UpdateCompilationRequest updateCompilationRequest) {
        log.info("ADMIN: Запрос на обновление подборки событий {}", updateCompilationRequest);

        Compilation compilationFromDb = compilationRepository.getCompilationById(updateCompilationRequest.getId())
                .orElseThrow(() -> new NotFoundException(NOT_EXISTING_COMPILATION));

        if (updateCompilationRequest.getEvents() != null) {
            compilationFromDb.setEventList(updateCompilationRequest.getEvents());
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilationFromDb.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilationFromDb.setTitle(updateCompilationRequest.getTitle());
        }
        Compilation compilationSaved = compilationRepository.save(compilationFromDb);

        log.info("Подборка событий обновлена");
        return compilationSaved;
    }

    @Override
    public List<Compilation> getAll(PublicCompilationParam publicCompilationParam) {
        log.info("PUBLIC: Запрос на получение всех подборок событий");

        Pageable pageable = new OffsetBasedPageRequest(publicCompilationParam.getFrom(), publicCompilationParam.getSize());
        List<Compilation> compilationList = compilationRepository.findAll(pageable).getContent();

        log.info("PUBLIC: Получен список всех подборок событий {}", compilationList);
        return compilationList;
    }

    @Override
    public Compilation getById(Long id) {
        log.info("PUBLIC: Запрос на получение подборки событий по id {}", id);

        Compilation compilation = compilationRepository.getCompilationById(id)
                .orElseThrow(() -> new NotFoundException(NOT_EXISTING_COMPILATION));
        if (compilation.getEventList() != null) {
            List<Event> eventList = eventRepository.getAllByIdIn(compilation.getEventList());
            compilation.setEvents(eventList);
        }

        log.info("PUBLIC: Получена подборка событий {} по id {}", compilation, id);
        return compilation;
    }
}
