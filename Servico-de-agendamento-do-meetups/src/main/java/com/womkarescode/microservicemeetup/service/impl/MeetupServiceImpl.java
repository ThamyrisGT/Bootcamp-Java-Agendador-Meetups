package com.womkarescode.microservicemeetup.service.impl;

import com.womkarescode.microservicemeetup.controller.dto.MeetupFilterDTO;
import com.womkarescode.microservicemeetup.model.entity.Meetup;
import com.womkarescode.microservicemeetup.model.entity.Registration;
import com.womkarescode.microservicemeetup.repository.MeetupRepository;
import com.womkarescode.microservicemeetup.service.MeetupService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MeetupServiceImpl implements MeetupService {

    private MeetupRepository repository;

    public MeetupServiceImpl(MeetupRepository repository) {
        this.repository = repository;
    }

    @Override
    public Meetup save(Meetup meetup) {
        return repository.save(meetup);
    }

    @Override
    public Optional<Meetup> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Meetup update(Meetup newMeetup) {
        return  repository.save(newMeetup);
    }

    @Override
    public Page<Meetup> findAll(MeetupFilterDTO filterDTO, Pageable pageable) {
        return repository.findByRegistrationOnMeetup(filterDTO.getRegistration(),filterDTO.getEvent(),pageable);
    }

    @Override
    public Page<Meetup> getRegistrationsByMeetup(Registration registration, Pageable pageable) {
        return repository.findByRegistration(registration,pageable);
    }
}
