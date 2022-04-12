package com.womkarescode.microservicemeetup.service;


import com.womkarescode.microservicemeetup.controller.dto.MeetupFilterDTO;
import com.womkarescode.microservicemeetup.model.entity.Meetup;
import com.womkarescode.microservicemeetup.model.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MeetupService {

    Meetup save(Meetup meetup);

    Optional<Meetup> getById(Long id);

    Meetup update(Meetup newMeetup);

    Page<Meetup> findAll(MeetupFilterDTO filterDTO, Pageable pageable);

    Page<Meetup> getRegistrationsByMeetup(Registration registration, Pageable pageable);
}
