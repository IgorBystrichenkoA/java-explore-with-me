package ru.practicum.ewm.event.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.request.ParticipationRequestDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
    private List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

    public void addConfirmedRequest(ParticipationRequestDto request) {
        confirmedRequests.add(request);
    }

    public void addRejectedRequest(ParticipationRequestDto request) {
        rejectedRequests.add(request);
    }
}
