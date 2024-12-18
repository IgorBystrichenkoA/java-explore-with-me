package ru.practicum.ewm.request;

import ru.practicum.ewm.request.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createUserRequest(@PathVariable Long userId,
                                                     @RequestParam Long eventId) {
        PrivateRequestParam privateRequestParam = new PrivateRequestParam();
        privateRequestParam.setUserId(userId);
        privateRequestParam.setEventId(eventId);
        return RequestMapper.toParticipationRequestDto(requestService.createUserRequest(privateRequestParam));
    }

    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        return RequestMapper.toListParticipationRequestDto(requestService.getUserRequests(userId));
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelUserParticipation(@PathVariable Long userId,
                                                           @PathVariable Long requestId) {
        PrivateRequestParam privateRequestParam = new PrivateRequestParam();
        privateRequestParam.setUserId(userId);
        privateRequestParam.setRequestId(requestId);
        return RequestMapper.toParticipationRequestDto(requestService.cancelUserRequest(privateRequestParam));
    }
}
