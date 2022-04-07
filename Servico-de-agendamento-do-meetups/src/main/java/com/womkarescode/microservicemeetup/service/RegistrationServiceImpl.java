package com.womkarescode.microservicemeetup.service;

import com.womkarescode.microservicemeetup.exception.BusinessException;
import com.womkarescode.microservicemeetup.model.entity.Registration;
import com.womkarescode.microservicemeetup.repository.RegistrationRepository;
import org.springframework.data.domain.*;

import java.util.Optional;

public class RegistrationServiceImpl implements  RegistrationService{

    RegistrationRepository registrationRepository;

    public RegistrationServiceImpl(RegistrationRepository repository) {
        this.registrationRepository = repository;
    }

    @Override
    public Registration save(Registration registration) {
        if(registrationRepository.existsByRegistration(registration.getRegistration())){
            throw  new BusinessException("Registration already created");
        }
        return registrationRepository.save(registration);
    }

    @Override
    public Optional<Registration> getRegistrationById(Long  id) {
        return this.registrationRepository.findById(id);
    }

    @Override
    public Registration update(Registration registration) {
        if(registration == null || registration.getId() == null){
            throw new IllegalArgumentException("Registration id can't be null");
        }
        return registrationRepository.save(registration);
    }

    @Override
    public void delete(Registration registration) {
        if(registration == null || registration.getId() == null){
            throw new IllegalArgumentException("Registration id can't be null");
        }
        registrationRepository.delete(registration);
    }

    @Override
    public Page<Registration> find(Registration filter, Pageable pageRequest) {
        Example<Registration> example = Example.of(filter,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                );

        return registrationRepository.findAll(example,pageRequest);
    }

    @Override
    public Optional<Registration> getRegistrationByRegistrationAttribute(String registrationAttribute) {
        return registrationRepository.findByRegistration(registrationAttribute);
    }
}
