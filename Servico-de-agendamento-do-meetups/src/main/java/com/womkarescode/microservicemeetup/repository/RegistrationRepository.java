package com.womkarescode.microservicemeetup.repository;

import com.womkarescode.microservicemeetup.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    boolean existsByRegistration(String registration);

    Optional <Registration> findByRegistration(String registrationAttribute);
}
