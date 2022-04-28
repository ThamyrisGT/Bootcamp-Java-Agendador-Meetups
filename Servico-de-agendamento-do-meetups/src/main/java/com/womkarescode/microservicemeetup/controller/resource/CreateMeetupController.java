package com.womkarescode.microservicemeetup.controller.resource;

import com.womkarescode.microservicemeetup.controller.data.CreateMeetupData;
import com.womkarescode.microservicemeetup.model.dto.CreateMeetupDTO;
import com.womkarescode.microservicemeetup.model.entity.CreateMeetup;
import com.womkarescode.microservicemeetup.service.CreateMeetupService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/create-meetups")
@RequiredArgsConstructor
public class CreateMeetupController {

    private final CreateMeetupService service;
    private final ModelMapper modelMapper;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateMeetupDTO createMeetup (@RequestBody @Valid CreateMeetupData meetupData){
        CreateMeetup createEvent = modelMapper.map(meetupData, CreateMeetup.class);
        createEvent = service.saveNewEventMeetup(createEvent);
        return modelMapper.map(createEvent, CreateMeetupDTO.class);
    }

    @PutMapping("{id}")
    public CreateMeetupDTO updateEvent(@PathVariable Long id, @RequestBody @Valid CreateMeetupData meetupData){
        return service.getEventById(id).map( event -> {

            event.setEvent(meetupData.getEvent());
            event.setEventDate(meetupData.getEventDate());
            event.setLinkMeetup(meetupData.getLinkMeetup());
            event.setGuestSpeaker(meetupData.getGuestSpeaker());
            event = service.updateEventMeetup(event);
            return modelMapper.map(event, CreateMeetupDTO.class);

        }).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long id){
        CreateMeetup eventMeetup = service.getEventById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.deleteEventMeetup(eventMeetup);
    }

    @GetMapping
    public Page<CreateMeetupDTO> findAllEvents(CreateMeetupData meetupData, Pageable pageRequest ){
        CreateMeetup filter = modelMapper.map(meetupData, CreateMeetup.class);
        Page<CreateMeetup> result = service.findAllEventMeetup(filter, pageRequest);
        List<CreateMeetupDTO> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, CreateMeetupDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<CreateMeetupDTO>( list, pageRequest, result.getTotalElements() );
    }

}
