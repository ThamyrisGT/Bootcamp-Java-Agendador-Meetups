package com.womkarescode.microservicemeetup.controller.resource;

import com.womkarescode.microservicemeetup.controller.dto.MeetupDTO;
import com.womkarescode.microservicemeetup.controller.dto.MeetupFilterDTO;
import com.womkarescode.microservicemeetup.controller.dto.RegistrationDTO;
import com.womkarescode.microservicemeetup.model.entity.Meetup;
import com.womkarescode.microservicemeetup.model.entity.Registration;
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
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody MeetupDTO meetupDTO){
        Registration registration = registrationService.getRegistrationByRegistrationAttribute(meetupDTO.getRegistrationAttribute())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        Meetup meetup =Meetup.builder()
                .registration(registration)
                .event(meetupDTO.getEvent())
                .meetupDate("10/10/2021")
                .build();
        meetup = meetupService.save(meetup);
        return meetup.getId();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<MeetupDTO> findAll(MeetupFilterDTO meetupFilterDTO, Pageable pageRequest){
        Page<Meetup> result = meetupService.findAll(meetupFilterDTO, pageRequest);

        List<MeetupDTO> meetups = result
                .getContent()
                .stream()
                .map(e ->{
                    Registration registration = e.getRegistration();
                    RegistrationDTO registrationDTO = modelMapper.map(registration,RegistrationDTO.class);

                    MeetupDTO meetupDTO =modelMapper.map(e,MeetupDTO.class);
                    meetupDTO.setRegistration(registrationDTO);
                    return meetupDTO;
                }).collect(Collectors.toList());

        return new PageImpl<MeetupDTO>(meetups,pageRequest,result.getTotalElements());
    }
}
