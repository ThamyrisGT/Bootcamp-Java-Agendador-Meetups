package com.womkarescode.microservicemeetup.service.impl;

import com.womkarescode.microservicemeetup.exception.BusinessException;
import com.womkarescode.microservicemeetup.model.entity.CreateMeetup;
import com.womkarescode.microservicemeetup.repository.CreateMeetupRepository;
import com.womkarescode.microservicemeetup.service.CreateMeetupService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreateMeetupServiceImpl implements CreateMeetupService {

    private CreateMeetupRepository repository;

    public CreateMeetupServiceImpl(CreateMeetupRepository repository) {
        this.repository = repository;
    }

    @Override
    public CreateMeetup saveNewEventMeetup(CreateMeetup createEvent) {
        if(repository.findByEvent(createEvent.getEvent()).isPresent()){
            throw new BusinessException("Event already created");
        }
        return repository.save(createEvent);
    }

    @Override
    public Optional<CreateMeetup> findByEvent(String event) {
        return repository.findByEvent(event);
    }

    @Override
    public Optional<CreateMeetup> getEventById(Long id) {
        return repository.findById(id);
    }

    @Override
    public CreateMeetup updateEventMeetup(CreateMeetup event) {
        if(event == null || event.getId() == null){
            throw new IllegalArgumentException("Event id must not be null");
        }
        return repository.save(event);
    }

    @Override
    public void deleteEventMeetup(CreateMeetup eventMeetup) {
        if(repository.findByEvent(eventMeetup.getEvent()).isEmpty()){
            throw new IllegalArgumentException("Event id must exist");
        }
         repository.delete(eventMeetup);
    }

    @Override
    public Page<CreateMeetup> findAllEventMeetup(CreateMeetup filter, Pageable pageRequest) {
        Example<CreateMeetup> example = Example.of(filter,
                ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher( ExampleMatcher.StringMatcher.CONTAINING )
        ) ;
        return repository.findAll(example, pageRequest);
    }
}
