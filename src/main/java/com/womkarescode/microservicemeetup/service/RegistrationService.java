package com.womkarescode.microservicemeetup.service;

import com.womkarescode.microservicemeetup.model.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RegistrationService {

    Registration save (Registration any);

    Optional<Registration> getRegistrationById(Long id);

    Registration update(Registration registration);

    void delete(Registration registration);

    Page<Registration> find(Registration filter, Pageable pageRequest);

    Optional<Registration> getRegistrationByRegistrationAttribute(String registrationAttribute);

    Optional<Registration> getByRegistration(String registrationAttribute);
}
