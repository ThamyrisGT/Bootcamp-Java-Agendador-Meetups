package com.womkarescode.microservicemeetup.controller.resource;

import com.womkarescode.microservicemeetup.controller.form.CreateMeetupForm;
import com.womkarescode.microservicemeetup.model.dto.CreateMeetupDTO;
import com.womkarescode.microservicemeetup.model.entity.CreateMeetup;
import com.womkarescode.microservicemeetup.service.CreateMeetupService;
import lombok.RequiredArgsConstructor;
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
    public CreateMeetupDTO createMeetup (@RequestBody @Valid CreateMeetupForm meetupForm){
        CreateMeetup createEvent = modelMapper.map(meetupForm, CreateMeetup.class);
        createEvent = service.saveNewEventMeetup(createEvent);
        return modelMapper.map(createEvent, CreateMeetupDTO.class);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public CreateMeetupDTO getMeetupEvent(@PathVariable Long id){
        return service.getEventById(id)
                .map(event -> modelMapper.map(event,CreateMeetupDTO.class))
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("{id}")
    public CreateMeetupDTO updateEvent(@PathVariable Long id, @RequestBody @Valid CreateMeetupForm meetupForm){
        return service.getEventById(id).map( event -> {

            event.setEvent(meetupForm.getEvent());
            event.setEventDate(meetupForm.getEventDate());
            event.setLinkMeetup(meetupForm.getLinkMeetup());
            event.setGuestSpeaker(meetupForm.getGuestSpeaker());
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
    public Page<CreateMeetupDTO> findAllEvents(CreateMeetupForm meetupForm, Pageable pageRequest ){
        CreateMeetup filter = modelMapper.map(meetupForm, CreateMeetup.class);
        Page<CreateMeetup> result = service.findAllEventMeetup(filter, pageRequest);
        List<CreateMeetupDTO> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, CreateMeetupDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<CreateMeetupDTO>( list, pageRequest, result.getTotalElements());
    }

}
