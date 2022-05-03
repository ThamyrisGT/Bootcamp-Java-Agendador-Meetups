package com.womkarescode.microservicemeetup.controller.resource;

import com.womkarescode.microservicemeetup.model.dto.MeetupDTO;
import com.womkarescode.microservicemeetup.model.dto.MeetupFilterDTO;
import com.womkarescode.microservicemeetup.model.dto.RegistrationDTO;
import com.womkarescode.microservicemeetup.model.entity.CreateMeetup;
import com.womkarescode.microservicemeetup.model.entity.Meetup;
import com.womkarescode.microservicemeetup.model.entity.Registration;
import com.womkarescode.microservicemeetup.service.CreateMeetupService;
import com.womkarescode.microservicemeetup.service.MeetupService;
import com.womkarescode.microservicemeetup.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meetups")
@RequiredArgsConstructor
public class MeetupController {

    private final MeetupService meetupService;
    private final RegistrationService registrationService;
    private final CreateMeetupService createMeetupService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MeetupDTO createRegisterForEvent(@RequestBody MeetupFilterDTO filterDTO){
        Registration registration = registrationService.getByRegistration(filterDTO.getRegistration())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        CreateMeetup newEvent = createMeetupService.findByEvent(filterDTO.getEvent())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        Meetup entity = Meetup.builder()
                .eventDetails(newEvent)
                .registration(registration)
                .build();

        entity = meetupService.save(entity);
        MeetupDTO dto = modelMapper.map(entity, MeetupDTO.class);
        return dto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<MeetupDTO> findAll(MeetupFilterDTO meetupFilterDTO, Pageable pageRequest){
        Page<Meetup> result = meetupService.findAll(meetupFilterDTO, pageRequest);
        List<MeetupDTO> meetups = result
                .getContent()
                .stream()
                .map(entity -> {

                    Registration registration = entity.getRegistration();
                    RegistrationDTO registrationDTO = modelMapper.map(registration, RegistrationDTO.class);

                    MeetupDTO meetupDTO = modelMapper.map(entity, MeetupDTO.class);
                    meetupDTO.setRegistration(registrationDTO);
                    return meetupDTO;

                }).collect(Collectors.toList());
        return new PageImpl<MeetupDTO>(meetups, pageRequest, result.getTotalElements());
    }
}
