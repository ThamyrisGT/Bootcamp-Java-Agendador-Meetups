package com.womkarescode.microservicemeetup.service;

import com.womkarescode.microservicemeetup.exception.BusinessException;
import com.womkarescode.microservicemeetup.model.Registration;
import com.womkarescode.microservicemeetup.repository.RegistrationRepository;

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
    public Optional<Registration> getResgistrationById(Long  id) {
        return this.registrationRepository.findById(id);
    }
}
