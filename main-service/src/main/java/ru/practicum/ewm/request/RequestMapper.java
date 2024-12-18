package ru.practicum.ewm.request;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class RequestMapper {
    public ParticipationRequestDto toParticipationRequestDto(Request request) {
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setCreated(request.getCreated());
        participationRequestDto.setEvent(request.getEvent().getId());
        participationRequestDto.setId(request.getId());
        participationRequestDto.setRequester(request.getRequester().getId());
        participationRequestDto.setStatus(String.valueOf(request.getStatus()));
        return participationRequestDto;
    }

    public List<ParticipationRequestDto> toListParticipationRequestDto(List<Request> requestList) {
        if (requestList != null) {
            return requestList.stream().map(RequestMapper::toParticipationRequestDto).toList();
        } else {
            return List.of();
        }
    }
}
