package com.womkarescode.microservicemeetup.service;

import com.womkarescode.microservicemeetup.model.Registration;

import java.util.Optional;

public interface RegistrationService {

    Registration save (Registration any);

    Optional<Registration> getResgistrationById(Long id);
}
